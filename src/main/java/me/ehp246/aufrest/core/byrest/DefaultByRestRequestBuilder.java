package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BindingDescriptor;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InvocationAuthProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestRequestRecord;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.AnnotatedArgument;
import me.ehp246.aufrest.core.reflection.ProxyInvocation;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class DefaultByRestRequestBuilder {

    private final static Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(PathVariable.class,
            RequestParam.class, RequestHeader.class, AuthHeader.class);
    private final static Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);

    private final Optional<InvocationAuthProvider> byRestProxyAuthProvider;
    private final InvocationAuthProviderResolver methodAuthProviderResolver;
    private final ByRestProxyConfig byRestConfig;
    private final PropertyResolver propertyResolver;
    private final Duration timeout;
    private final BodyHandlerProvider bodyHandlerProvider;

    DefaultByRestRequestBuilder(final ByRestProxyConfig byRestConfig,
            final InvocationAuthProviderResolver methodAuthProviderResolver, final PropertyResolver propertyResolver,
            final BodyHandlerProvider bodyHandlerProvider) {
        super();
        this.byRestConfig = byRestConfig;
        this.methodAuthProviderResolver = methodAuthProviderResolver;
        this.propertyResolver = propertyResolver;
        this.bodyHandlerProvider = bodyHandlerProvider;

        this.timeout = Optional.ofNullable(byRestConfig.timeout()).filter(OneUtil::hasValue)
                .map(propertyResolver::resolve).map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
                .orElse(null);

        this.byRestProxyAuthProvider = Optional.of(byRestConfig.auth()).map(auth -> {
            return switch (auth.scheme()) {
            case SIMPLE -> {
                if (auth.value().size() < 1) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                final var simple = propertyResolver.resolve(auth.value().get(0)).toString();
                yield i -> simple;
            }
            case BASIC -> {
                if (auth.value().size() < 2) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                final var basic = new BasicAuth(propertyResolver.resolve(auth.value().get(0)),
                        propertyResolver.resolve(auth.value().get(1))).value();
                yield i -> basic;
            }
            case BEARER -> {
                if (auth.value().size() < 1) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                final var bearer = new BearerToken(propertyResolver.resolve(auth.value().get(0))).value();
                yield i -> bearer;
            }
            case BEAN -> {
                if (auth.value().size() < 1) {
                    throw new IllegalArgumentException("Missing required arguments for " + auth.scheme().name());
                }
                final var provider = methodAuthProviderResolver.get(auth.value().get(0));
                yield i -> provider.get(i);
            }
            case NONE -> i -> (String) null;
            default -> null;
            };
        });
    }

    /**
     * Creates a {@link RestRequest} from a {@link ProxyInvocation}.
     * 
     */
    @SuppressWarnings("unchecked")
    RestRequest from(ProxyInvocation invocation) {
        final var optionalOfMapping = invocation.findOnMethod(OfMapping.class);

        final var pathParams = invocation.findArgumentOfAnnotation(PathVariable.class, PathVariable::value).entrySet()
                .stream().map(entry -> {
                    if (entry.getKey().equals("")) {
                        return entry;
                    }
                    entry.setValue(UriUtils.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
                    return entry;
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final var unnamedPathMap = pathParams.get("");
        if (unnamedPathMap != null && unnamedPathMap instanceof Map) {
            ((Map<String, Object>) unnamedPathMap).entrySet().stream().forEach(entry -> pathParams
                    .putIfAbsent(entry.getKey(), UriUtils.encode(entry.getValue().toString(), StandardCharsets.UTF_8)));
        }

        final var queryParamArgs = invocation.<String, Object, RequestParam>mapArgumentsOfAnnotation(RequestParam.class,
                RequestParam::value);

        // Should not have a name for the query parameter map
        final var unnamedQueryMap = queryParamArgs.remove("");

        if (unnamedQueryMap != null && unnamedQueryMap.size() > 0 && unnamedQueryMap.get(0) instanceof Map<?, ?> map) {
            map.entrySet().stream()
                    .forEach(e -> queryParamArgs.merge(e.getKey().toString(), List.of(e.getValue()), (o, p) -> {
                        o.add(p.get(0));
                        return o;
                    }));
        }

        final var queryParams = OneUtil.toQueryParamMap(queryParamArgs);

        final var uri = UriComponentsBuilder
                .fromUriString(propertyResolver.resolve(this.byRestConfig.uri()
                        + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")))
                .buildAndExpand(pathParams).toUriString();

        final var method = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue).or(() -> {
            final var invokedMethodName = invocation.getMethodName().toUpperCase();
            return HttpUtils.METHOD_NAMES.stream().filter(name -> invokedMethodName.startsWith(name)).findAny();
        }).map(String::toUpperCase).orElseThrow(() -> new RuntimeException("Un-defined HTTP method"));

        final var accept = optionalOfMapping.map(OfMapping::accept).orElse(this.byRestConfig.accept());

        final var headers = new HashMap<String, List<String>>();

        // Set accept-encoding at a lower priority.
        if (byRestConfig.acceptGZip()) {
            headers.put(HttpHeaders.ACCEPT_ENCODING.toLowerCase(Locale.US), List.of("gzip"));
        }

        // Collect headers from invocation arguments. Last step, highest priority.
        invocation.streamOfAnnotatedArguments(RequestHeader.class)
                .forEach(new Consumer<AnnotatedArgument<RequestHeader>>() {
                    @Override
                    public void accept(final AnnotatedArgument<RequestHeader> annoArg) {
                        newValue(annoArg.annotation().value(), annoArg.argument());
                    }

                    private void newValue(final Object key, final Object newValue) {
                        if (newValue == null) {
                            return;
                        }

                        if (newValue instanceof Iterable<?> iter) {
                            iter.forEach(v -> newValue(key, v));
                            return;
                        }

                        if (newValue instanceof Map<?, ?> map) {
                            map.entrySet().forEach(entry -> {
                                newValue(entry.getKey(), entry.getValue());
                            });
                            return;
                        }

                        getMapped(key).add(newValue.toString());
                    }

                    private List<String> getMapped(final Object key) {
                        return headers.computeIfAbsent(key.toString(), k -> new ArrayList<String>());
                    }
                });

        final var returnTypes = bodyType(Stream
                .concat(Arrays.stream(new Class<?>[] { invocation.getReturnType() }),
                        Arrays.stream(
                                invocation.getMethodValueOf(Reifying.class, Reifying::value, () -> new Class<?>[] {})))
                .collect(Collectors.toList()));

        final var authSupplier = invocation.streamOfAnnotatedArguments(AuthHeader.class).findFirst()
                .map(arg -> (Supplier<String>) () -> OneUtil.toString(arg.argument()))
                .orElse(optionalOfMapping.map(OfMapping::authProvider).filter(OneUtil::hasValue)
                        .map(name -> (Supplier<String>) () -> methodAuthProviderResolver.get(name).get(invocation))
                        .orElse(byRestProxyAuthProvider
                                .map(provider -> (Supplier<String>) () -> provider.get(invocation)).orElse(null)));

        final var contentType = Optional.ofNullable(optionalOfMapping.map(OfMapping::contentType)
                .filter(OneUtil::hasValue).orElseGet(this.byRestConfig::contentType)).filter(OneUtil::hasValue)
                .orElseGet(() -> {
                    // TODO: Determine content type by the body object type.
                    // Defaults to JSON.
                    return HttpUtils.APPLICATION_JSON;
                });

        final var bodyHandler = Optional.ofNullable(invocation.findArgumentsOfType(BodyHandler.class))
                .map(args -> args.size() == 0 ? null : args.get(0))
                .orElseGet(() -> bodyHandlerProvider
                        .get(new BindingDescriptor(returnTypes.get(0), byRestConfig.errorType(),
                                returnTypes.size() == 0 ? List.of() : returnTypes.subList(1, returnTypes.size()),
                                invocation.getMethodDeclaredAnnotations())));

        return new RestRequestRecord(UUID.randomUUID().toString(), uri, method, timeout, authSupplier, contentType,
                accept, resolveBody(invocation), headers, queryParams, bodyHandler);
    }

    private Object resolveBody(ProxyInvocation invocation) {
        return invocation.findArgumentsOfType(BodyPublisher.class).stream().findFirst().map(v -> (Object) v)
                .orElseGet(() -> {
                    final var payload = invocation.filterPayloadArgs(PARAMETER_ANNOTATED, PARAMETER_RECOGNIZED);

                    return payload.size() >= 1 ? payload.get(0) : null;
                });
    }

    private static List<Class<?>> bodyType(final List<Class<?>> types) {
        if (types.size() == 0) {
            throw new IllegalArgumentException("Missing required " + Reifying.class.getName());
        }

        final var head = types.get(0);
        if (head.isAssignableFrom(HttpResponse.class) || head.isAssignableFrom(CompletableFuture.class)) {
            return bodyType(new ArrayList<>(types.subList(1, types.size())));
        }
        return types;
    }
}
