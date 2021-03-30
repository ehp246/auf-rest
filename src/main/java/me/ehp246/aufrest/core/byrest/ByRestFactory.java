package me.ehp246.aufrest.core.byrest;

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
import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestResponse;
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
	private final ClientConfig clientConfig;

	@Autowired
	public ByRestFactory(final RestFnProvider clientProvider, final ClientConfig clientConfig,
			final PlaceholderResolver phResolver) {
		super();
		this.phResolver = phResolver;
		this.clientProvider = clientProvider;
		this.clientConfig = clientConfig;
	}

	public ByRestFactory(final RestFnProvider clientProvider, final PlaceholderResolver phResolver) {
		this(clientProvider, new ClientConfig() {
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
				if (auth.args().length < 1) {
					throw new IllegalArgumentException(
							"Missing required arguments for " + auth.scheme().name()
									+ " on "
							+ interfaceName);
				}
				return phResolver.resolve(auth.args()[0])::toString;
			case BASIC:
				if (auth.args().length < 2) {
					throw new IllegalArgumentException(
							"Missing required arguments for " + auth.scheme().name()
									+ " on "
							+ interfaceName);
				}
				return new BasicAuth(phResolver.resolve(auth.args()[0]),
						phResolver.resolve(auth.args()[1]))::value;
			case BEARER:
				if (auth.args().length < 1) {
					throw new IllegalArgumentException(
							"Missing required arguments for " + auth.scheme().name()
									+ " on "
							+ interfaceName);
				}
				return new BearerToken(phResolver.resolve(auth.args()[0]))::value;
			case NONE:
				return () -> null;
			default:
				return null;
			}
		});

		final var restFn = clientProvider.get(clientConfig);

		final var reqByRest = new ReqByRest(path -> phResolver.resolve(byRest.value() + path), timeout,
				localAuthSupplier, byRest.contentType(), byRest.accept());

		return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						final var invoked = new ProxyInvoked(byRestInterface, proxy, method, args);
						final var req = reqByRest.from(invoked);
						final var respSupplier = (Supplier<RestResponse>) () -> {
							ThreadContext.put(AufRestConstants.REQUEST_ID, req.id());
							try {
								return restFn.apply(req);
							} finally {
								ThreadContext.remove(AufRestConstants.REQUEST_ID);
							}
						};

						return resolveReturn(invoked, respSupplier);
					}

					private Object resolveReturn(ProxyInvoked invoked, Supplier<RestResponse> call)
							throws Throwable {
						final var restResponse = (RestResponse) InvocationOutcome.invoke(call)
								.accept(invoked.getThrows());

						final var returnType = invoked.getReturnType();
						final var httpResponse = restResponse.httpResponse();

						if (returnType.isAssignableFrom(RestResponse.class)) {
							return restResponse;
						}

						// If the return type is HttpResponse, returns it as is without any processing
						// regardless the status code.
						if (returnType.isAssignableFrom(HttpResponse.class)) {
							return httpResponse;
						}

						if (httpResponse.statusCode() >= 300) {
							throw new UnhandledResponseException(restResponse.restRequest(), httpResponse);
						}

						// Discard the response.
						if (returnType == Void.class || returnType == void.class) {
							return null;
						}

						return httpResponse.body();
					}
				});

	}
}
