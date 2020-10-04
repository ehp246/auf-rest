package me.ehp246.aufrest.core.byrest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.Environment;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.Receiver;
import me.ehp246.aufrest.core.reflection.ProxyInvoked;
import me.ehp246.aufrest.core.util.Utils;

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
		final var timeout = byRest.map(ByRest::timeout).filter(Utils::hasValue)
				.map(text -> env.resolveRequiredPlaceholders(text))
				.map(text -> Utils.orThrow(() -> Duration.parse(text),
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

		final var client = clientProvider.get();

		return (T) Proxy.newProxyInstance(byRestInterface.getClassLoader(), new Class[] { byRestInterface },
				(InvocationHandler) (proxy, method, args) -> {
					final var proxyInvoked = new ProxyInvoked<>(proxy, method, args);
					final var returnTypes = bodyType(
							Stream.concat(Arrays.stream(new Class<?>[] { proxyInvoked.getReturnType() }),
									Arrays.stream(proxyInvoked.getMethodValueOf(Reifying.class, Reifying::value,
											() -> new Class<?>[] {})))
									.collect(Collectors.toList()));

					final var bodyReceiver = new Receiver() {

						@Override
						public Class<?> type() {
							return returnTypes.get(0);
						}

						@Override
						public List<Class<?>> reifying() {
							return returnTypes.size() == 0 ? List.of() : returnTypes.subList(1, returnTypes.size());
						}

						@Override
						public List<? extends Annotation> annotations() {
							return proxyInvoked.getMethodDeclaredAnnotations();
						}
					};

					final var request = new ByRestInvocation(proxyInvoked, env) {

						@Override
						public Duration timeout() {
							return timeout;
						}

						@Override
						public Supplier<String> authSupplier() {
							return localAuthSupplier.orElse(null);
						}

						@Override
						public Receiver bodyReceiver() {
							return bodyReceiver;
						}

					};
					return request.setResponseSupplier(() -> client.apply(request)).returnInvocation();
				});

	}

	private static List<Class<?>> bodyType(final List<Class<?>> types) {
		if (types.size() == 0) {
			throw new IllegalArgumentException();
		}

		final var head = types.get(0);
		if (head.isAssignableFrom(HttpResponse.class) || head.isAssignableFrom(CompletableFuture.class)) {
			return bodyType(new ArrayList<>(types.subList(1, types.size())));
		}
		return types;
	}
}
