package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestObserver;

/**
 * For each call for a HTTP client, the provider should ask the client-builder
 * supplier for a new builder. For each HTTP request, the provider should ask
 * the request-builder supplier for a new builder. The provider should not
 * cache/re-use any builders.
 *
 * @author Lei Yang
 */
public final class DefaultRestFnProvider implements RestFnProvider {
	private final static Logger LOGGER = LogManager.getLogger(DefaultRestFnProvider.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier;
	private final RequestBuilder reqBuilder;
	private final List<RestObserver> observers;

	public DefaultRestFnProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier) {
		this(clientBuilderSupplier, req -> null, null);
	}

	@Autowired
	public DefaultRestFnProvider(final RequestBuilder reqBuilder, final List<RestObserver> observers) {
		this.clientBuilderSupplier = HttpClient::newBuilder;
		this.reqBuilder = reqBuilder;
		this.observers = observers == null ? List.of() : observers;
	}

	public DefaultRestFnProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier, final RequestBuilder restToHttp,
			final List<RestObserver> observers) {
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilder = restToHttp;
		this.observers = observers == null ? List.of() : observers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RestFn get(final ClientConfig clientConfig) {
		final var clientBuilder = clientBuilderSupplier.get();
		if (clientConfig.connectTimeout() != null) {
			clientBuilder.connectTimeout(clientConfig.connectTimeout());
		}

		final HttpClient client = clientBuilder.build();
		final BodyHandlerProvider bodyHandlerProvider = clientConfig.bodyHandlerProvider();

		return req -> {
			final var httpReq = reqBuilder.apply(req);

			observers.stream().forEach(obs -> obs.preSend(httpReq, req));

			final HttpResponse<Object> httpResponse;
			// Try/catch on send only.
			try {
				httpResponse = (HttpResponse<Object>) client.send(httpReq, bodyHandlerProvider.get(req));
			} catch (Exception e) {
				LOGGER.atError().log("Failed to send request: " + e.getMessage(), e);

				observers.stream().forEach(obs -> obs.onException(e, httpReq, req));

				throw new RuntimeException(e);
			}

			observers.stream().forEach(obs -> obs.postSend(httpResponse, req));

			return httpResponse;
		};
	}
}
