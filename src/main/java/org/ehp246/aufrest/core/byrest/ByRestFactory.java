package org.ehp246.aufrest.core.byrest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.ehp246.aufrest.api.rest.HttpFnConfig;
import org.ehp246.aufrest.api.rest.HttpFnProvider;
import org.ehp246.aufrest.core.reflection.ObjectToText;
import org.ehp246.aufrest.core.reflection.ProxyInvoked;
import org.ehp246.aufrest.core.reflection.TextToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 *
 * @author Lei Yang
 *
 */
public class ByRestFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByRestFactory.class);

	private final Environment env;
	private final HttpFnProvider httpFnProvider;
	private final HttpFnConfig clientConfig;
	private final ObjectToText toText;
	private final TextToObject fromText;

	public ByRestFactory(final HttpFnProvider httpFnprovider, final Environment env, final TextToObject fromText,
			final ObjectToText toText, final HttpFnConfig httpFnConfig) {
		super();
		this.env = env;
		this.httpFnProvider = httpFnprovider;
		this.toText = toText;
		this.fromText = fromText;
		this.clientConfig = httpFnConfig;

	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(final Class<T> annotatedInterface) {
		LOGGER.debug("Proxying {}", annotatedInterface.getCanonicalName());
		final var httpFn = httpFnProvider.get(clientConfig);

		return (T) Proxy.newProxyInstance(annotatedInterface.getClassLoader(), new Class[] { annotatedInterface },
				(InvocationHandler) (proxy, method, args) -> {
					final var request = new ByRestInvocation(new ProxyInvoked<>(proxy, method, args), env, fromText,
							toText);
					return request.setResponseSupplier(httpFn.apply(request));
				});

	}
}
