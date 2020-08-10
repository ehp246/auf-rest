package me.ehp246.aufrest.core.byrest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.Environment;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;

/**
 *
 * @author Lei Yang
 *
 */
public class ByRestFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByRestFactory.class);

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

		final var byRest = Optional.ofNullable(byRestInterface.getAnnotation(ByRest.class));
		final var timeout = byRest.map(ByRest::timeout).filter(millis -> millis > 0).map(Duration::ofMillis)
				.orElse(null);
		final var authHeader = byRest.map(ByRest::auth).map(auth -> {
			switch (auth.type()) {
			case BEARER:
				return HttpUtils.bearerToken(env.resolveRequiredPlaceholders(auth.value()));
			case ASIS:
				return env.resolveRequiredPlaceholders(auth.value());
			case BASIC:
				return HttpUtils.basicAuth(env.resolveRequiredPlaceholders(auth.value()));
			case BEAN:
				return beanFactory.getBean(auth.value(), Supplier.class).get().toString();
			case DEFAULT:
			default:
			}
			return null;
		}).orElse(null);

		final var client = clientProvider.get();

		return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
				(InvocationHandler) (proxy, method, args) -> {
					final var request = new ByRestInvocation(new ProxyInvoked<>(proxy, method, args), env) {

						@Override
						public Duration timeout() {
							return timeout;
						}

						@Override
						public String authentication() {
							return authHeader;
						}

					};
					return request.setResponseSupplier(() -> client.apply(request)).returnInvocation();
				});

	}
}
