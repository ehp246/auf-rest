package me.ehp246.aufrest.core.byrest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
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
    public <T> T newInstance(final Class<T> byRestInterface, final ByRestProxyConfig byRestConfig) {
        final var interfaceName = byRestInterface.getCanonicalName();

        LOGGER.atDebug().log("Instantiating {}", interfaceName);

        final var httpFn = clientProvider.get(clientConfig);

        final RestRequestFromInvocation restFromInvocation;
        try {
            restFromInvocation = new RestRequestFromInvocation(byRestConfig, methodAuthProviderMap, propertyResolver);
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

        return this.newInstance(byRestInterface, new ByRestProxyConfig() {
            private final Auth auth = new Auth() {

                @Override
                public List<String> value() {
                    return Arrays.asList(byRest.auth().value());
                }

                @Override
                public AuthScheme scheme() {
                    return AuthScheme.valueOf(byRest.auth().scheme().name());
                }

            };

            @Override
            public String uri() {
                return propertyResolver.resolve(byRest.value());
            }

            @Override
            public String timeout() {
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

            @Override
            public Class<?> errorType() {
                return byRest.errorType();
            }

            @Override
            public Auth auth() {
                return auth;
            }

        });
    }
}
