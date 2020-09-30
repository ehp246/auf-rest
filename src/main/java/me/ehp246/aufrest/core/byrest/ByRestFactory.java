package me.ehp246.aufrest.core.byrest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.Environment;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;

/**
 *
 * @author Lei Yang
 *
 */
public class ByRestFactory {
	private final static Logger LOGGER = LogManager.getLogger(ByRestFactory.class);

	private final ListableBeanFactory beanFactory;
	private final Environment env;
	private final Supplier<ClientFn> clientProvider;

	public ByRestFactory(final Supplier<ClientFn> clientProvider, final Environment env,
			final ListableBeanFactory beanFactory) {
		super();
		this.env = env;
		this.clientProvider = clientProvider;
		this.beanFactory = beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> byRestInterface) {
		LOGGER.debug("Instantiating {}@ByRest", byRestInterface.getCanonicalName());

		final var byRest = Optional.of(byRestInterface.getAnnotation(ByRest.class));
		final var timeout = byRest.map(ByRest::timeout).filter(millis -> millis > 0).map(Duration::ofMillis)
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

		final var client = clientProvider.get();

		return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
				(InvocationHandler) (proxy, method, args) -> {
					final var request = new ByRestInvocation(new ProxyInvoked<>(proxy, method, args), env) {

						@Override
						public Duration timeout() {
							return timeout;
						}

						@Override
						public Supplier<String> authSupplier() {
							return localAuthSupplier.orElse(null);
						}

					};
					return request.setResponseSupplier(() -> client.apply(request)).returnInvocation();
				});

	}
}
