package me.ehp246.aufrest.core.byrest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Optional;

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
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig.AuthConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.reflection.ProxyInvocation;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 *
 */
public final class ByRestFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByRestFactory.class);

    private final PropertyResolver propertyResolver;
    private final RestFnProvider clientProvider;
    private final RestClientConfig clientConfig;
    private final InvocationAuthProviderResolver methodAuthProviderMap;
    private final BodyHandlerProvider bodyHandlerProvider;

    @Autowired
    public ByRestFactory(final RestFnProvider clientProvider, final RestClientConfig clientConfig,
            final PropertyResolver propertyResolver, final InvocationAuthProviderResolver methodAuthProviderMap,
            final BodyHandlerProvider bodyHandlerProvider) {
        super();
        this.propertyResolver = propertyResolver;
        this.clientProvider = clientProvider;
        this.clientConfig = clientConfig;
        this.methodAuthProviderMap = methodAuthProviderMap;
        this.bodyHandlerProvider = bodyHandlerProvider;
    }

    public ByRestFactory(final RestFnProvider clientProvider, final PropertyResolver propertyResolver) {
        this(clientProvider, new RestClientConfig(), propertyResolver, name -> null, b -> BodyHandlers.discarding());
    }

    public ByRestFactory(final RestFnProvider clientProvider) {
        this(clientProvider, new RestClientConfig(), s -> s, name -> null, b -> BodyHandlers.discarding());
    }

    public ByRestFactory(RestFnProvider restFnProvider, RestClientConfig restClientConfig,
            PropertyResolver propertyResolver, InvocationAuthProviderResolver invocationAuthProviderResolver) {
        this(restFnProvider, restClientConfig, propertyResolver, invocationAuthProviderResolver,
                b -> BodyHandlers.discarding());
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byRestInterface, final ByRestProxyConfig byRestConfig) {
        final var interfaceName = byRestInterface.getCanonicalName();

        LOGGER.atDebug().log("Instantiating {}", interfaceName);

        final var httpFn = clientProvider.get(clientConfig);

        final DefaultByRestRequestBuilder restFromInvocation;
        try {
            restFromInvocation = new DefaultByRestRequestBuilder(byRestConfig, methodAuthProviderMap,
                    propertyResolver, bodyHandlerProvider);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to instantiate " + byRestInterface.getCanonicalName(), e);
        }

        return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
                (proxy, method, args) -> {
                    if (method.getName().equals("toString")) {
                        return this.toString();
                    }
                    if (method.getName().equals("hashCode")) {
                        return this.hashCode();
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

                    final var invoked = new ProxyInvocation(byRestInterface, proxy, method, args);
                    final var req = restFromInvocation.from(invoked);
                    final var outcome = RestFnOutcome.invoke(() -> {
                        ThreadContext.put(HttpUtils.REQUEST_ID, req.id());
                        try {
                            return httpFn.apply(req);
                        } finally {
                            ThreadContext.remove(HttpUtils.REQUEST_ID);
                        }
                    });

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
                });

    }

    public <T> T newInstance(final Class<T> byRestInterface) {
        final var byRest = Optional.of(byRestInterface.getAnnotation(ByRest.class)).get();
        final var timeout = Optional.of(propertyResolver.resolve(byRest.timeout())).filter(OneUtil::hasValue)
                .orElse(null);

        return this.newInstance(byRestInterface,
                new ByRestProxyConfig(propertyResolver.resolve(byRest.value()),
                        new AuthConfig(Arrays.asList(byRest.auth().value()),
                                AuthScheme.valueOf(byRest.auth().scheme().name())),
                        timeout, byRest.accept(), byRest.contentType(), byRest.acceptGZip(), byRest.errorType()));
    }
}
