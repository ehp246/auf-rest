package me.ehp246.aufrest.core.byrest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestResponse;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 *
 */
public final class ByRestFactory {
	private final static Logger LOGGER = LogManager.getLogger(ByRestFactory.class);

	private final ListableBeanFactory beanFactory;
	private final Environment env;
	private final RestFnProvider clientProvider;
	private final ClientConfig clientConfig;

	@Autowired
	public ByRestFactory(final RestFnProvider clientProvider, final ClientConfig clientConfig, final Environment env,
			final ListableBeanFactory beanFactory) {
		super();
		this.env = env;
		this.clientProvider = clientProvider;
		this.clientConfig = clientConfig;
		this.beanFactory = beanFactory;
	}

	public ByRestFactory(final RestFnProvider clientProvider, final Environment env,
			final ListableBeanFactory beanFactory) {
		this(clientProvider, new ClientConfig() {
		}, env, beanFactory);
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> byRestInterface) {
		LOGGER.atDebug().log("Instantiating {}", byRestInterface.getCanonicalName());

		final var byRest = Optional.of(byRestInterface.getAnnotation(ByRest.class));

		final var timeout = byRest.map(ByRest::timeout).map(text -> env.resolveRequiredPlaceholders(text))
				.filter(OneUtil::hasValue).map(text -> OneUtil.orThrow(() -> Duration.parse(text),
						e -> new IllegalArgumentException("Invalid Timeout: " + text, e)))
				.orElse(null);
		final Optional<Supplier<String>> localAuthSupplier = byRest.map(ByRest::auth).map(auth -> {
			switch (auth.type()) {
			case ASIS:
				return env.resolveRequiredPlaceholders(auth.value())::toString;
			case BASIC:
				return new BasicAuth(env.resolveRequiredPlaceholders(auth.value()))::value;
			case BEAN:
				return new Supplier<String>() {
					// Look up bean once.
					private final Supplier<?> bean = beanFactory.getBean(auth.value(), Supplier.class);

					@Override
					public String get() {
						// Get should be called once for each invocation.
						return bean.get().toString();
					}
				};
			case BEARER:
				return new BearerToken(env.resolveRequiredPlaceholders(auth.value()))::value;
			default:
				return null;
			}
		});

		final var restFn = clientProvider.get(clientConfig);
		final var reqByRest = new ReqByRest(
				(path) -> env.resolveRequiredPlaceholders(byRest.map(ByRest::value).get() + path), timeout,
				localAuthSupplier);

		return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						final var invoked = new ProxyInvoked<>(proxy, method, args);
						final var req = reqByRest.from(invoked);
						final var respSupplier = (Supplier<RestResponse>) () -> {
							ThreadContext.put(AufRestConstants.REQUEST_ID, req.id());
							try {
								return restFn.apply(req);
							} finally {
								ThreadContext.remove(AufRestConstants.REQUEST_ID);
							}
						};

						if (invoked.isSync()) {
							// Synchronous invocation. Let's do it now.
							return parseAndReturn(invoked.getReturnType(), respSupplier.get());
						}

						// Copy the header context.
						final var context = HeaderContext.map();
						return CompletableFuture.supplyAsync(() -> {
							try {
								// Set the context on the new thread.
								HeaderContext.set(context);

								final var reifying = invoked.getMethodValueOf(Reifying.class, Reifying::value,
										() -> new Class<?>[] {});
								if (reifying.length == 0) {
									throw new IllegalArgumentException("Missing required " + Reifying.class.getName());
								}
								return parseAndReturn(reifying[0], respSupplier.get());
							} finally {
								// Clear the header context before exiting.
								HeaderContext.clear();
							}
						});
					}

					private Object parseAndReturn(Class<?> returnType, RestResponse restResp) {
						final var httpResponse = restResp.httpResponse();

						if (returnType.isAssignableFrom(RestResponse.class)) {
							return restResp;
						}

						// If the return type is HttpResponse, returns it as is without any processing
						// regardless the status code.
						if (returnType.isAssignableFrom(HttpResponse.class)) {
							return httpResponse;
						}

						if (httpResponse.statusCode() >= 300) {
							throw new UnhandledResponseException(restResp.restRequest(), httpResponse);
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
