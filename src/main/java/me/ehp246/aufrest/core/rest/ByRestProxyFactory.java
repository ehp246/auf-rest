package me.ehp246.aufrest.core.rest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;

/**
 * Factory of {@linkplain ByRest} beans. Depended by
 * {@linkplain ByRestRegistrar}.
 *
 * @author Lei Yang
 * @see {@link EnableByRest}, {@link ByRestRegistrar}
 * @since 1.0
 */
public final class ByRestProxyFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByRestProxyFactory.class);

    private final Map<Method, InvocationBinder> parsedCache = new ConcurrentHashMap<>();

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
                    public Object invoke(final Object proxy, final Method method, final Object[] args)
                            throws Throwable {
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

                        final var bound = parsedCache.computeIfAbsent(method, m -> methodParser.parse(method))
                                .apply(proxy, args);

                        final var outcome = RestFnOutcome.invoke(() -> restFn.applyForResponse(bound.request(),
                                bound.requestBodyDescriptor(), bound.responseDescriptor()));

                        /*
                         * Was a response received?
                         *
                         * RestFn throws an UnhandledResponseException if a response is received but
                         * with a wrong status code. Unpack it for the return mapper.
                         *
                         */
                        final HttpResponse<?> httpResponse;
                        if (outcome.received() instanceof final HttpResponse<?> received) {
                            httpResponse = received;
                        } else if (outcome.received() instanceof final UnhandledResponseException responseException) {
                            httpResponse = responseException.getCause().httpResponse();
                        } else {
                            httpResponse = null;
                        }

                        if (httpResponse != null) {
                            return bound.returnMapper().apply(bound.request(), httpResponse);
                        }

                        /*
                         * No response. Must mean something bad happened before or during send.
                         */
                        throw (Throwable) outcome.received();
                    }
                });
    }
}
