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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.spi.Invocation;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.ReflectedProxyMethod;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class ProxyMethodParser {
    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);

    private final PropertyResolver propertyResolver;
    private final InvocationAuthProviderResolver methodAuthProviderMap;

    ProxyMethodParser(final PropertyResolver propertyResolver,
            final InvocationAuthProviderResolver methodAuthProviderMap) {
        this.propertyResolver = propertyResolver;
        this.methodAuthProviderMap = methodAuthProviderMap;
    }

    @SuppressWarnings("unchecked")
    public ParsedMethodRequestBuilder parse(final Method method, final ByRestProxyConfig proxyConfig) {
        final var reflected = new ReflectedProxyMethod(method);
        final var optionalOfMapping = reflected.findOnMethod(OfMapping.class);

        final var verb = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue)
                .or(() -> HttpUtils.METHOD_NAMES.stream()
                        .filter(name -> method.getName().toUpperCase().startsWith(name)).findAny())
                .map(String::toUpperCase)
                .orElseThrow(() -> new IllegalArgumentException("Un-defined HTTP method on " + method.toString()));

        final var uriBuilder = UriComponentsBuilder.fromUriString(propertyResolver.resolve(
                proxyConfig.uri() + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")));

        final Map<String, Integer> pathMap = new HashMap<>();
        final Map<Integer, String> queryMap = new HashMap<>();
        final Map<String, List<String>> defaultHeaders = new HashMap<>();

        reflected.allParametersWith(PathVariable.class).forEach(p -> {
            pathMap.put(p.parameter().getAnnotation(PathVariable.class).value(), p.index());
        });

        reflected.allParametersWith(RequestParam.class).forEach(p -> {
            queryMap.put(p.index(), p.parameter().getAnnotation(RequestParam.class).value());
        });

        // Set accept-encoding at a lower priority.
        if (proxyConfig.acceptGZip()) {
            defaultHeaders.put(HttpUtils.ACCEPT_ENCODING.toLowerCase(Locale.US), List.of("gzip"));
        }

        final var accept = optionalOfMapping.map(OfMapping::accept).filter(OneUtil::hasValue)
                .orElse(proxyConfig.accept());
        final var contentType = optionalOfMapping.map(OfMapping::contentType).filter(OneUtil::hasValue)
                .orElse(proxyConfig.contentType());

        final var authHeaders = reflected.allParametersWith(AuthHeader.class);
        if (authHeaders.size() > 1) {
            throw new IllegalArgumentException(
                    "Too many " + AuthHeader.class.getSimpleName() + " found on " + method.getName());
        }

        final Function<Object[], Supplier<String>> authSupplierFn;
        if (authHeaders.size() == 1) {
            final var param = authHeaders.get(0);
            final var index = param.index();
            if (Supplier.class.isAssignableFrom(param.parameter().getType())) {
                authSupplierFn = args -> (Supplier<String>) (args[index]);
            } else {
                authSupplierFn = args -> args[index] == null ? () -> null : args[index]::toString;
            }
        } else {
            authSupplierFn = optionalOfMapping.map(OfMapping::authProvider).filter(OneUtil::hasValue).map(
                    name -> {
                        final var invocationAuthProvider = methodAuthProviderMap.get(name);
                        return (Function<Object[], Supplier<String>>) args -> () -> invocationAuthProvider
                                .get(new Invocation() {
                                    final List<?> asList = args == null ? List.of() : Arrays.asList(args);

                                    @Override
                                    public Object target() {
                                        return null;
                                    }

                                    @Override
                                    public Method method() {
                                        return method;
                                    }

                                    @Override
                                    public List<?> args() {
                                        return asList;
                                    }
                                });
                    }).orElse(null);
        }


        return new ParsedMethodRequestBuilder(verb, accept, contentType, uriBuilder, authSupplierFn, pathMap, queryMap,
                defaultHeaders);
    }
}
