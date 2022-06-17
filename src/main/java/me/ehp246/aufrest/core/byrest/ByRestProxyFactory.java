package me.ehp246.aufrest.core.byrest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig.AuthConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.spi.BodyHandlerResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
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
    private final BodyHandlerResolver bodyHandlerResolver;

    @Autowired
    public ByRestProxyFactory(final RestFnProvider restFnProvider, final RestClientConfig clientConfig,
            final PropertyResolver propertyResolver, final ProxyMethodParser methodParser,
            final BodyHandlerResolver bodyHandlerResolver) {
        super();
        this.propertyResolver = propertyResolver;
        this.clientProvider = restFnProvider;
        this.clientConfig = clientConfig;
        this.methodParser = methodParser;
        this.bodyHandlerResolver = bodyHandlerResolver;
    }

    public ByRestProxyFactory(RestFnProvider restFnProvider, RestClientConfig clientConfig,
            PropertyResolver propertyResolver, ProxyMethodParser parser) {
        this(restFnProvider, clientConfig, propertyResolver, parser, name -> BodyHandlers.discarding());
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
                        final var returnType = method.getReturnType();
                        if (method.isDefault()) {
                            return MethodHandles.privateLookupIn(byRestInterface, MethodHandles.lookup())
                                    .findSpecial(byRestInterface, method.getName(),
                                            MethodType.methodType(returnType, method.getParameterTypes()),
                                            byRestInterface)
                                    .bindTo(proxy).invokeWithArguments(args);
                        }

                        final var req = parsedCache
                                .computeIfAbsent(method, m -> methodParser.parse(method, proxyConfig))
                                .apply(proxy, args);
                        final var outcome = RestFnOutcome.invoke(() -> {
                            ThreadContext.put(HttpUtils.REQUEST_ID, req.id());
                            try {
                                return httpFn.apply(req);
                            } finally {
                                ThreadContext.remove(HttpUtils.REQUEST_ID);
                            }
                        });

                        final var threws = List.of(method.getExceptionTypes());
                        final var httpResponse = (HttpResponse<?>) outcome.orElseThrow(threws);

                        // If the return type is HttpResponse, returns it as is without any processing
                        // regardless the status code.
                        if (returnType.isAssignableFrom(HttpResponse.class)) {
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
                            if (canThrow(threws, ex.getClass())) {
                                throw ex;
                            }

                            throw new UnhandledResponseException(ex);
                        }

                        // Discard the response.
                        if (returnType == void.class && returnType == Void.class) {
                            return null;
                        }

                        return httpResponse.body();
                    }

                    private static boolean canThrow(List<Class<?>> threws, Class<?> type) {
                        return threws.stream().filter(t -> t.isAssignableFrom(type)).findAny().isPresent();
                    }
                });

    }

    public <T> T newInstance(final Class<T> byRestInterface) {
        final var byRest = byRestInterface.getAnnotation(ByRest.class);
        final var timeout = Optional.of(propertyResolver.resolve(byRest.timeout())).filter(OneUtil::hasValue)
                .map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
                .orElse(null);

        /*
         * Delayed URI resolution to accommodate ${local.server.port}.
         */
        return this.newInstance(byRestInterface,
                new ByRestProxyConfig(byRest.value(),
                        new AuthConfig(Arrays.asList(byRest.auth().value()),
                                AuthScheme.valueOf(byRest.auth().scheme().name())),
                        timeout, byRest.accept(), byRest.contentType(), byRest.acceptGZip(), byRest.errorType(),
                        Optional.ofNullable(byRest.responseBodyHandler()).filter(OneUtil::hasValue)
                                .map(propertyResolver::resolve).map(bodyHandlerResolver::get).orElse(null)));
    }
}
