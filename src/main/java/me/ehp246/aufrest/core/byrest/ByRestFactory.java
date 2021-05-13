package me.ehp246.aufrest.core.byrest;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ServerErrorException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.PlaceholderResolver;
import me.ehp246.aufrest.core.reflection.InvocationOutcome;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 *
 */
public final class ByRestFactory {
    private final static Logger LOGGER = LogManager.getLogger(ByRestFactory.class);

    private final PlaceholderResolver phResolver;
    private final RestFnProvider clientProvider;
    private final RestClientConfig clientConfig;

    @Autowired
    public ByRestFactory(final RestFnProvider clientProvider, final RestClientConfig clientConfig,
            final PlaceholderResolver phResolver) {
        super();
        this.phResolver = phResolver;
        this.clientProvider = clientProvider;
        this.clientConfig = clientConfig;
    }

    public ByRestFactory(final RestFnProvider clientProvider, final PlaceholderResolver phResolver) {
        this(clientProvider, new RestClientConfig() {
        }, phResolver);
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> byRestInterface) {
        final var interfaceName = byRestInterface.getCanonicalName();

        LOGGER.atDebug().log("Instantiating @ByRest {}", interfaceName);

        // Annotation required.
        final var byRest = Optional.of(byRestInterface.getAnnotation(ByRest.class)).get();

        final var timeout = Optional.of(phResolver.resolve(byRest.timeout())).filter(OneUtil::hasValue)
                .map(text -> OneUtil.orThrow(() -> Duration.parse(text),
                        e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
                .orElse(null);

        final Optional<Supplier<String>> localAuthSupplier = Optional.of(byRest.auth()).map(auth -> {
            switch (auth.scheme()) {
            case SIMPLE:
                if (auth.value().length < 1) {
                    throw new IllegalArgumentException(
                            "Missing required arguments for " + auth.scheme().name() + " on " + interfaceName);
                }
                return phResolver.resolve(auth.value()[0])::toString;
            case BASIC:
                if (auth.value().length < 2) {
                    throw new IllegalArgumentException(
                            "Missing required arguments for " + auth.scheme().name() + " on " + interfaceName);
                }
                return new BasicAuth(phResolver.resolve(auth.value()[0]), phResolver.resolve(auth.value()[1]))::value;
            case BEARER:
                if (auth.value().length < 1) {
                    throw new IllegalArgumentException(
                            "Missing required arguments for " + auth.scheme().name() + " on " + interfaceName);
                }
                return new BearerToken(phResolver.resolve(auth.value()[0]))::value;
            case NONE:
                return () -> null;
            default:
                return null;
            }
        });

        final var httpFn = clientProvider.get(clientConfig);

        final var reqByRest = new ReqByRest(path -> phResolver.resolve(byRest.value() + path), timeout,
                localAuthSupplier, byRest.contentType(), byRest.accept());

        return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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

                        final var invoked = new ProxyInvoked(byRestInterface, proxy, method, args);
                        final var req = reqByRest.from(invoked);
                        final var respSupplier = (Supplier<HttpResponse<?>>) () -> {
                            ThreadContext.put(HttpUtils.REQUEST_ID, req.id());
                            try {
                                return httpFn.apply(req);
                            } finally {
                                ThreadContext.remove(HttpUtils.REQUEST_ID);
                            }
                        };

                        return callAndResolve(req, invoked, respSupplier);
                    }

                    private Object callAndResolve(RestRequest req, ProxyInvoked invoked, Supplier<HttpResponse<?>> call)
                            throws Throwable {
                        final var httpResponse = (HttpResponse<?>) InvocationOutcome.invoke(call)
                                .orElseThrow(invoked.getThrows());

                        // If the return type is HttpResponse, returns it as is without any processing
                        // regardless the status code.
                        if (invoked.canReturn(HttpResponse.class)) {
                            return httpResponse;
                        }

                        if (httpResponse.statusCode() >= 300) {
                            if (httpResponse.statusCode() >= 500 && invoked.canThrow(ServerErrorException.class)) {
                                throw new ServerErrorException(req, httpResponse);
                            }

                            if (httpResponse.statusCode() >= 400 && httpResponse.statusCode() < 500
                                    && invoked.canThrow(ClientErrorException.class)) {
                                throw new ClientErrorException(req, httpResponse);
                            }

                            throw new UnhandledResponseException(req, httpResponse);
                        }

                        // Discard the response.
                        if (!invoked.hasReturn()) {
                            return null;
                        }

                        return httpResponse.body();
                    }
                });

    }
}
