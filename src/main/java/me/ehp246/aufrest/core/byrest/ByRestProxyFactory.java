package me.ehp246.aufrest.core.byrest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;

/**
 *
 * @author Lei Yang
 *
 */
public final class ByRestProxyFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByRestProxyFactory.class);

    private final Map<Method, InvocationRequestBinder> parsedCache = new ConcurrentHashMap<>();

    private final RestFnProvider clientProvider;
    private final ClientConfig clientConfig;
    private final ProxyMethodParser methodParser;

    public ByRestProxyFactory(final RestFnProvider restFnProvider, final ClientConfig clientConfig,
            final ProxyMethodParser methodParser) {
        super();
        this.clientProvider = restFnProvider;
        this.clientConfig = clientConfig;
        this.methodParser = methodParser;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byRestInterface) {
        LOGGER.atDebug().log("Instantiating {}", byRestInterface::getCanonicalName);

        return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
                new InvocationHandler() {
                    private final RestFn restFn = clientProvider.get(clientConfig);

                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
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

                        final var bound = parsedCache
                                .computeIfAbsent(method, m -> methodParser.parse(method))
                                .apply(proxy, args);
                        final var req = bound.request();

                        final var outcome = RestFnOutcome
                                .invoke(() -> restFn.apply(req, bound.consumer()));

                        final var threws = List.of(method.getExceptionTypes());

                        final var httpResponse = (HttpResponse<?>) outcome.orElseThrow(threws);

                        // If the return type is HttpResponse, returns it as-is without any processing
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

                        // Discard the response which should be 2xx.
                        if (returnType == void.class && returnType == Void.class) {
                            return null;
                        }

                        if (returnType.isAssignableFrom(HttpHeaders.class)) {
                            return httpResponse.headers();
                        }

                        return httpResponse.body();
                    }

                    private static boolean canThrow(final List<Class<?>> threws, final Class<?> type) {
                        return threws.stream().filter(t -> t.isAssignableFrom(type)).findAny().isPresent();
                    }
                });
    }
}
