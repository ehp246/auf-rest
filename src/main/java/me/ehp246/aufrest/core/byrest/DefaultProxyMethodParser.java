package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.Invocation;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.ReflectedParameter;
import me.ehp246.aufrest.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultProxyMethodParser implements ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);

    private final PropertyResolver propertyResolver;
    private final InvocationAuthProviderResolver methodAuthProviderMap;
    private final BodyHandlerResolver bodyHandlerResolver;
    private final BindingBodyHandlerProvider bindingBodyHandlerProvider;

    public DefaultProxyMethodParser(final PropertyResolver propertyResolver,
            final InvocationAuthProviderResolver methodAuthProviderMap, final BodyHandlerResolver bodyHandlerResolver,
            final BindingBodyHandlerProvider bindingBodyHandlerProvider) {
        this.propertyResolver = propertyResolver;
        this.methodAuthProviderMap = methodAuthProviderMap;
        this.bodyHandlerResolver = bodyHandlerResolver;
        this.bindingBodyHandlerProvider = bindingBodyHandlerProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ParsedMethodRequestBuilder parse(final Method method, final ByRestProxyConfig byRestConfig) {
        final var reflected = new ReflectedProxyMethod(method);
        final var optionalOfMapping = reflected.findOnMethod(OfMapping.class);

        final var verb = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue)
                .or(() -> HttpUtils.METHOD_NAMES.stream()
                        .filter(name -> method.getName().toUpperCase().startsWith(name)).findAny())
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Un-defined HTTP method on " + method.toString()));

        final var uriBuilder = UriComponentsBuilder.fromUriString(propertyResolver.resolve(
                byRestConfig.uri() + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")));

        final Map<String, Integer> pathMap = new HashMap<>();
        reflected.allParametersWith(PathVariable.class).forEach(p -> {
            pathMap.put(p.parameter().getAnnotation(PathVariable.class).value(), p.index());
        });

        final Map<Integer, String> queryMap = new HashMap<>();
        reflected.allParametersWith(RequestParam.class).forEach(p -> {
            queryMap.put(p.index(), p.parameter().getAnnotation(RequestParam.class).value());
        });

        // Application headers
        final var headerMap = reflected.allParametersWith(RequestHeader.class).stream().collect(Collectors.toMap(
                ReflectedParameter::index, p -> p.parameter().getAnnotation(RequestHeader.class).value().toString()));

        // Set accept-encoding at a lower priority.
        final Map<String, List<String>> reservedHeaders = new HashMap<>();
        if (byRestConfig.acceptGZip()) {
            reservedHeaders.put(HttpUtils.ACCEPT_ENCODING.toLowerCase(Locale.US), List.of("gzip"));
        }

        final var accept = optionalOfMapping.map(OfMapping::accept).filter(OneUtil::hasValue)
                .orElse(byRestConfig.accept());

        final var contentType = optionalOfMapping.map(OfMapping::contentType).filter(OneUtil::hasValue)
                .or(() -> Optional.ofNullable(byRestConfig.contentType())).filter(OneUtil::hasValue)
                .orElse(HttpUtils.APPLICATION_JSON);

        final var authHeaders = reflected.allParametersWith(AuthHeader.class);
        if (authHeaders.size() > 1) {
            throw new IllegalArgumentException(
                    "Too many " + AuthHeader.class.getSimpleName() + " found on " + method.getName());
        }

        final BiFunction<Object, Object[], Supplier<String>> authSupplierFn;
        if (authHeaders.size() == 1) {
            final var param = authHeaders.get(0);
            final var index = param.index();
            if (Supplier.class.isAssignableFrom(param.parameter().getType())) {
                authSupplierFn = (target, args) -> (Supplier<String>) (args[index]);
            } else {
                authSupplierFn = (target, args) -> args[index] == null ? () -> null : args[index]::toString;
            }
        } else if (optionalOfMapping.map(OfMapping::authProvider).filter(OneUtil::hasValue).isPresent()) {
            final var provider = methodAuthProviderMap.get(optionalOfMapping.map(OfMapping::authProvider).get());
            authSupplierFn = (target, args) -> () -> {
                return provider.get(new Invocation() {
                    private final List<?> list = args == null ? List.of() : Arrays.asList(args);

                    @Override
                    public Object target() {
                        return target;
                    }

                    @Override
                    public Method method() {
                        return method;
                    }

                    @Override
                    public List<?> args() {
                        return list;
                    }
                });
            };
        } else {
            authSupplierFn = Optional.ofNullable(byRestConfig.auth()).map(auth -> {
                return switch (auth.scheme()) {
                case SIMPLE -> {
                    if (auth.value().size() < 1) {
                        throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                    }
                    final var simple = propertyResolver.resolve(auth.value().get(0));
                    yield (BiFunction<Object, Object[], Supplier<String>>) (target, args) -> simple::toString;
                }
                case BASIC -> {
                    if (auth.value().size() < 2) {
                        throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                    }
                    final var basic = new BasicAuth(propertyResolver.resolve(auth.value().get(0)),
                            propertyResolver.resolve(auth.value().get(1)));
                    yield (BiFunction<Object, Object[], Supplier<String>>) (target, args) -> basic::value;
                }
                case BEARER -> {
                    if (auth.value().size() < 1) {
                        throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                    }
                    final var bearer = new BearerToken(propertyResolver.resolve(auth.value().get(0)));
                    yield (BiFunction<Object, Object[], Supplier<String>>) (target, args) -> bearer::value;
                }
                case BEAN -> {
                    if (auth.value().size() < 1) {
                        throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                    }
                    final var provider = methodAuthProviderMap.get(propertyResolver.resolve(auth.value().get(0)));
                    yield (BiFunction<Object, Object[], Supplier<String>>) (target, args) -> () -> provider.get(null);
                }
                case NONE -> (BiFunction<Object, Object[], Supplier<String>>) (target, args) -> () -> null;
                default -> (BiFunction<Object, Object[], Supplier<String>>) (target, args) -> null;
                };
            }).orElse(null);
        }

        return new ParsedMethodRequestBuilder(verb, accept, contentType, uriBuilder, authSupplierFn, pathMap, queryMap,
                headerMap, reservedHeaders);
    }
}