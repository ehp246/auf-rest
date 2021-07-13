package me.ehp246.aufrest.core.byrest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
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

    @Autowired
    public ByRestFactory(final RestFnProvider clientProvider, final RestClientConfig clientConfig,
            final PropertyResolver propertyResolver, final InvocationAuthProviderResolver methodAuthProviderMap) {
        super();
        this.propertyResolver = propertyResolver;
        this.clientProvider = clientProvider;
        this.clientConfig = clientConfig;
        this.methodAuthProviderMap = methodAuthProviderMap;
    }

    public ByRestFactory(final RestFnProvider clientProvider, final PropertyResolver propertyResolver) {
        this(clientProvider, new RestClientConfig() {
        }, propertyResolver, name -> null);
    }

    public ByRestFactory(final RestFnProvider clientProvider) {
        this(clientProvider, new RestClientConfig() {
        }, s -> s, name -> null);
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byRestInterface) {
        final var interfaceName = byRestInterface.getCanonicalName();

        LOGGER.atDebug().log("Instantiating @ByRest {}", interfaceName);

        // Annotation required.
        final var byRest = Optional.of(byRestInterface.getAnnotation(ByRest.class)).get();

        final var timeout = Optional.of(propertyResolver.resolve(byRest.timeout())).filter(OneUtil::hasValue)
                .map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
                .orElse(null);

        final Optional<Supplier<String>> proxyAuthSupplier = Optional.of(byRest.auth()).map(auth -> {
            switch (auth.scheme()) {
            case SIMPLE:
                if (auth.value().length < 1) {
                    throw new IllegalArgumentException(
                            "Missing required arguments for " + auth.scheme().name() + " on " + interfaceName);
                }
                return propertyResolver.resolve(auth.value()[0])::toString;
            case BASIC:
                if (auth.value().length < 2) {
                    throw new IllegalArgumentException(
                            "Missing required arguments for " + auth.scheme().name() + " on " + interfaceName);
                }
                return new BasicAuth(propertyResolver.resolve(auth.value()[0]), propertyResolver.resolve(auth.value()[1]))::value;
            case BEARER:
                if (auth.value().length < 1) {
                    throw new IllegalArgumentException(
                            "Missing required arguments for " + auth.scheme().name() + " on " + interfaceName);
                }
                return new BearerToken(propertyResolver.resolve(auth.value()[0]))::value;
            case NONE:
                return () -> null;
            default:
                return null;
            }
        });

        final var httpFn = clientProvider.get(clientConfig);

        final var restFromInvocation = new RestRequestFromInvocation(new ByRestProxyConfig() {

            @Override
            public String resolveUri(final String path) {
                return propertyResolver.resolve(byRest.value() + path);
            }

            @Override
            public Duration timeout() {
                return timeout;
            }

            @Override
            public String contentType() {
                return byRest.contentType();
            }

            @Override
            public boolean acceptGZip() {
                return byRest.acceptGZip();
            }

            @Override
            public String accept() {
                return byRest.accept();
            }
        }, methodAuthProviderMap, proxyAuthSupplier);

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
                    final var req = restFromInvocation.get(invoked);
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

                    if (httpResponse.statusCode() >= 600) {
                        throw new UnhandledResponseException(req, httpResponse);
                    }

                    if (httpResponse.statusCode() >= 500 && invoked.canThrow(ServerErrorResponseException.class)) {
                        throw new ServerErrorResponseException(req, httpResponse);
                    }

                    if (httpResponse.statusCode() >= 400 && invoked.canThrow(ClientErrorResponseException.class)) {
                        throw new ClientErrorResponseException(req, httpResponse);
                    }

                    if (httpResponse.statusCode() >= 300) {
                        if (invoked.canThrow(RedirectionResponseException.class)) {
                            throw new RedirectionResponseException(req, httpResponse);
                        }

                        throw new UnhandledResponseException(req, httpResponse);
                    }

                    // Discard the response.
                    if (!invoked.hasReturn()) {
                        return null;
                    }

                    return httpResponse.body();
                });

    }
}
