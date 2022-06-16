package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BindingDescriptor;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig.AuthConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InvocationAuthProvider;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.AnnotatedArgument;
import me.ehp246.aufrest.core.reflection.ProxyInvocation;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 *
 */
public final class ByRestProxyFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByRestProxyFactory.class);

    private final Map<Method, ProxyToRestFn> parsedCache = new ConcurrentHashMap<>();

    private final PropertyResolver propertyResolver;
    private final RestFnProvider clientProvider;
    private final RestClientConfig clientConfig;
    private final ProxyMethodParser methodParser;

    public ByRestProxyFactory(final RestFnProvider restFnProvider, final RestClientConfig clientConfig,
            final PropertyResolver propertyResolver, final ProxyMethodParser methodParser) {
        super();
        this.propertyResolver = propertyResolver;
        this.clientProvider = restFnProvider;
        this.clientConfig = clientConfig;
        this.methodParser = methodParser;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byRestInterface, final ByRestProxyConfig proxyConfig) {
        LOGGER.atDebug().log("Instantiating {}", byRestInterface::getCanonicalName);

        return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
                new InvocationHandler() {
                    private final RestFn httpFn = clientProvider.get(clientConfig);

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("toString")) {
                            return ByRestProxyFactory.this.toString();
                        }
                        if (method.getName().equals("hashCode")) {
                            return ByRestProxyFactory.this.hashCode();
                        }
                        if (method.getName().equals("equals")) {
                            return proxy == args[0];
                        }
                        if (method.isDefault()) {
                            return MethodHandles.privateLookupIn(byRestInterface, MethodHandles.lookup())
                                    .findSpecial(byRestInterface, method.getName(),
                                            MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                                            byRestInterface)
                                    .bindTo(proxy).invokeWithArguments(args);
                        }

                        final var req = parsedCache.computeIfAbsent(method, m -> methodParser.parse(method, proxyConfig))
                                .apply(proxy, args);
                        final var outcome = RestFnOutcome.invoke(() -> {
                            ThreadContext.put(HttpUtils.REQUEST_ID, req.id());
                            try {
                                return httpFn.apply(req);
                            } finally {
                                ThreadContext.remove(HttpUtils.REQUEST_ID);
                            }
                        });

                        // TODO: Remove
                        final var invoked = new ProxyInvocation(byRestInterface, proxy, method, args);
                        final var httpResponse = (HttpResponse<?>) outcome.orElseThrow(invoked.getThrows());

                        // If the return type is HttpResponse, returns it as is without any processing
                        // regardless the status code.
                        if (invoked.canReturn(HttpResponse.class)) {
                            return httpResponse;
                        }

                        // Should throw the more specific type if possible.
                        ErrorResponseException ex = null;
                        if (httpResponse.statusCode() >= 600) {
                            ex = new ErrorResponseException(req, httpResponse);
                        } else if (httpResponse.statusCode() >= 500) {
                            ex = new ServerErrorResponseException(req, httpResponse);
                        } else if (httpResponse.statusCode() >= 400) {
                            ex = new ClientErrorResponseException(req, httpResponse);
                        } else if (httpResponse.statusCode() >= 300) {
                            ex = new RedirectionResponseException(req, httpResponse);
                        }

                        if (ex != null) {
                            if (invoked.canThrow(ex.getClass())) {
                                throw ex;
                            }

                            throw new UnhandledResponseException(ex);
                        }

                        // Discard the response.
                        if (!invoked.hasReturn()) {
                            return null;
                        }

                        return httpResponse.body();
                    }
                });

    }

    public <T> T newInstance(final Class<T> byRestInterface) {
        final var byRest = Optional.of(byRestInterface.getAnnotation(ByRest.class)).get();
        final var timeout = Optional.of(propertyResolver.resolve(byRest.timeout())).filter(OneUtil::hasValue)
                .map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
                .orElse(null);

        return this.newInstance(byRestInterface,
                new ByRestProxyConfig(propertyResolver.resolve(byRest.value()),
                        new AuthConfig(Arrays.asList(byRest.auth().value()),
                                AuthScheme.valueOf(byRest.auth().scheme().name())),
                        timeout, byRest.accept(), byRest.contentType(), byRest.acceptGZip(), byRest.errorType(),
                        byRest.responseBodyHandler()));
    }

    @SuppressWarnings("unchecked")
    private Function<ProxyInvocation, RestRequest> newRequestFn(final ByRestProxyConfig byRestConfig) {
        final InvocationAuthProviderResolver methodAuthProviderMap = null;
        final Duration timeout = null;

        final Optional<InvocationAuthProvider> byRestProxyAuthProvider = Optional.of(byRestConfig.auth()).map(auth -> {
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
                final var provider = methodAuthProviderMap.get(auth.value().get(0));
                yield i -> provider.get(i);
            }
            case NONE -> i -> (String) null;
            default -> null;
            };
        });

        return new Function<ProxyInvocation, RestRequest>() {
            @Override
            public RestRequest apply(ProxyInvocation invocation) {
                final BodyHandlerResolver bodyHandlerResolver = null;
                final Set<Class<? extends Annotation>> PARAMETER_ANNOTATED = Set.of(PathVariable.class,
                        RequestParam.class, RequestHeader.class, AuthHeader.class);
                final Set<Class<?>> PARAMETER_RECOGNIZED = Set.of(BodyPublisher.class, BodyHandler.class);

                final var optionalOfMapping = invocation.findOnMethod(OfMapping.class);

                final var pathParams = invocation.findArgumentOfAnnotation(PathVariable.class, PathVariable::value)
                        .entrySet().stream().map(entry -> {
                            if (entry.getKey().equals("")) {
                                return entry;
                            }
                            entry.setValue(UriUtils.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
                            return entry;
                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                final var unnamedPathMap = pathParams.get("");
                if (unnamedPathMap != null && unnamedPathMap instanceof Map) {
                    ((Map<String, Object>) unnamedPathMap).entrySet().stream()
                            .forEach(entry -> pathParams.putIfAbsent(entry.getKey(),
                                    UriUtils.encode(entry.getValue().toString(), StandardCharsets.UTF_8)));
                }

                final var queryParamArgs = invocation.<String, Object, RequestParam>mapArgumentsOfAnnotation(
                        RequestParam.class, RequestParam::value);

                // Should not have a name for the query parameter map
                final var unnamedQueryMap = queryParamArgs.remove("");

                if (unnamedQueryMap != null && unnamedQueryMap.size() > 0
                        && unnamedQueryMap.get(0) instanceof Map<?, ?> map) {
                    map.entrySet().stream()
                            .forEach(e -> queryParamArgs.merge(e.getKey().toString(), List.of(e.getValue()), (o, p) -> {
                                o.add(p.get(0));
                                return o;
                            }));
                }

                final var queryParams = OneUtil.toQueryParamMap(queryParamArgs);

                final var uri = UriComponentsBuilder
                        .fromUriString(propertyResolver.resolve(byRestConfig.uri()
                                + optionalOfMapping.map(OfMapping::value).filter(OneUtil::hasValue).orElse("")))
                        .buildAndExpand(pathParams).toUriString();

                final var method = optionalOfMapping.map(OfMapping::method).filter(OneUtil::hasValue).or(() -> {
                    final var invokedMethodName = invocation.getMethodName().toUpperCase();
                    return HttpUtils.METHOD_NAMES.stream().filter(name -> invokedMethodName.startsWith(name)).findAny();
                }).map(String::toUpperCase).orElseThrow(() -> new RuntimeException("Un-defined HTTP method"));

                final var accept = optionalOfMapping.map(OfMapping::accept).orElse(byRestConfig.accept());

                final var headers = new HashMap<String, List<String>>();

                // Set accept-encoding at a lower priority.
                if (byRestConfig.acceptGZip()) {
                    headers.put(org.springframework.http.HttpHeaders.ACCEPT_ENCODING.toLowerCase(Locale.US),
                            List.of("gzip"));
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

                final var authSupplier = invocation.streamOfAnnotatedArguments(AuthHeader.class).findFirst()
                        .map(arg -> (Supplier<String>) () -> OneUtil.toString(arg.argument()))
                        .orElse(optionalOfMapping.map(OfMapping::authProvider).filter(OneUtil::hasValue)
                                .map(name -> (Supplier<String>) () -> methodAuthProviderMap.get(name).get(invocation))
                                .orElse(byRestProxyAuthProvider
                                        .map(provider -> (Supplier<String>) () -> provider.get(invocation))
                                        .orElse(null)));

                final var body = invocation.findArgumentsOfType(BodyPublisher.class).stream().findFirst()
                        .orElseGet(() -> {
                            final var payload = invocation.filterPayloadArgs(PARAMETER_ANNOTATED, PARAMETER_RECOGNIZED);

                            return payload.size() >= 1 ? payload.get(0) : null;
                        });

                final var contentType = Optional
                        .ofNullable(optionalOfMapping.map(OfMapping::contentType).filter(OneUtil::hasValue)
                                .orElseGet(byRestConfig::contentType))
                        .filter(OneUtil::hasValue).orElse(HttpUtils.APPLICATION_JSON);

                final BindingBodyHandlerProvider bindingBodyHandlerProvider = null;
                @SuppressWarnings("rawtypes")
                final var bodyHandler = Optional.ofNullable(invocation.findArgumentsOfType(BodyHandler.class))
                        .map(args -> args.size() == 0 ? null : args.get(0).argument()).map(v -> (BodyHandler) v)
                        .or(() -> optionalOfMapping.map(OfMapping::responseBodyHandler)
                                .map(name -> OneUtil.hasValue(name) ? name : byRestConfig.responseBodyHandler())
                                .filter(OneUtil::hasValue).map(bodyHandlerResolver::get))
                        .orElseGet(() -> bindingBodyHandlerProvider.get(bindingOf(invocation)));

                return new RestRequestRecord(UUID.randomUUID().toString(), uri, method, timeout, authSupplier,
                        contentType, accept, headers, queryParams, body == null ? null : body.argument(),
                        body == null ? null : () -> body.parameter().getType(), bodyHandler);

            }

            private BindingDescriptor bindingOf(ProxyInvocation invocation) {
                final var returnTypes = returnTypes(Stream
                        .concat(Arrays.stream(new Class<?>[] { invocation.getReturnType() }), Arrays.stream(
                                invocation.getMethodValueOf(Reifying.class, Reifying::value, () -> new Class<?>[] {})))
                        .collect(Collectors.toList()));

                return new BindingDescriptor(returnTypes.get(0), byRestConfig.errorType(),
                        returnTypes.size() == 0 ? List.of() : returnTypes.subList(1, returnTypes.size()),
                        invocation.getMethodDeclaredAnnotations());
            }
        };
    }

    private static List<Class<?>> returnTypes(final List<Class<?>> types) {
        if (types.size() == 0) {
            throw new IllegalArgumentException("Missing required " + Reifying.class.getName());
        }

        final var head = types.get(0);
        if (head.isAssignableFrom(HttpResponse.class) || head.isAssignableFrom(CompletableFuture.class)) {
            return returnTypes(new ArrayList<>(types.subList(1, types.size())));
        }
        return types;
    }
}
