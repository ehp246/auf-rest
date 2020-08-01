package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import me.ehp246.aufrest.api.rest.AuthenticationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.Request;

/**
 * For each call to return a HTTP client, the provider should ask the
 * client-builder supplier for a new builder. For each HTTP request, the
 * provider should ask the request-builder supplier for a new builder. The
 * provider should not cache/re-use any builders.
 *
 * @author Lei Yang
 *
 */
public class JdkClientProvider implements Supplier<ClientFn> {
	private final static Logger LOGGER = LoggerFactory.getLogger(JdkClientProvider.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier;
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier;
	private final Optional<AuthenticationProvider> authProvider;
	private final ClientConfig clientConfig;

	public JdkClientProvider(final ClientConfig clientConfig) {
		this(HttpClient::newBuilder, HttpRequest::newBuilder, clientConfig, null);
	}

	public JdkClientProvider(final ClientConfig clientConfig, final AuthenticationProvider authProvider) {
		this(HttpClient::newBuilder, HttpRequest::newBuilder, clientConfig, authProvider);
	}

	public JdkClientProvider(final Supplier<Builder> clientBuilderSupplier,
			final Supplier<java.net.http.HttpRequest.Builder> reqBuilderSupplier, final ClientConfig clientConfig,
			final AuthenticationProvider authProvider) {
		super();
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilderSupplier = reqBuilderSupplier;
		this.authProvider = Optional.ofNullable(authProvider);
		this.clientConfig = clientConfig;
	}

	@Override
	public ClientFn get() {
		final var clientBuilder = clientBuilderSupplier.get();
		if (clientConfig.connectTimeout() != null) {
			clientBuilder.connectTimeout(clientConfig.connectTimeout());
		}

		return new ClientFn() {
			private final HttpClient client = clientBuilder.build();

			@Override
			public HttpResponse<?> apply(final Request req) {
				final var uri = URI.create(req.uri());
				final var body = req.body();
				final var authHeader = Optional.ofNullable(req.authentication())
						.orElseGet(() -> authProvider.map(provider -> provider.get(uri))
								.filter(value -> value != null && !value.isBlank()).orElse(null));

				final var builder = newRequestBuilder()
						.method(req.method().toUpperCase(),
								body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body.toString()))
						.uri(uri);

				// Timeout
				Optional.ofNullable(req.timeout() == null ? clientConfig.requestTimeout() : req.timeout())
						.map(builder::timeout);

				// Auth
				Optional.ofNullable(authHeader).map(header -> builder.header(HttpUtils.AUTHORIZATION, header));

				final var httpRequest = builder.build();
				LOGGER.debug("Sending {} {}", req.method(), req.uri());

				HttpResponse<?> httpResponse;
				try {
					httpResponse = client.send(httpRequest, req.bodyHandler());
				} catch (IOException | InterruptedException e) {
					LOGGER.error("Failed to receive response", e);
					throw new RuntimeException(e);
				}

				LOGGER.debug("Received {}", httpResponse.toString());

				return httpResponse;
			}
		};
	}

	private HttpRequest.Builder newRequestBuilder() {
		return reqBuilderSupplier.get().header(HttpUtils.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpUtils.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
	}
}
