package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				final var authHeader = Optional.ofNullable(req.authentication())
						.orElseGet(() -> authProvider.map(provider -> provider.get(uri))
								.filter(value -> value != null && !value.isBlank()).orElse(null));

				final var builder = newRequestBuilder(req).method(req.method().toUpperCase(), bodyPublisher(req))
						.uri(uri);

				// Timeout
				Optional.ofNullable(req.timeout() == null ? clientConfig.responseTimeout() : req.timeout())
						.map(builder::timeout);

				// Optional Authentication
				Optional.ofNullable(authHeader).map(header -> builder.header(HttpUtils.AUTHORIZATION, header));

				final var httpRequest = builder.build();
				LOGGER.debug("Sending {} {}", req.method(), req.uri());

				HttpResponse<?> httpResponse;
				try {
					httpResponse = client.send(httpRequest, bodyHandler(req));
				} catch (IOException | InterruptedException e) {
					throw new RuntimeException("Failed to receive response", e);
				}

				LOGGER.debug("Received {}", httpResponse.toString());
				return httpResponse;
			}

			private BodyHandler<?> bodyHandler(final Request req) {
				final var receiver = req.receiver();
				final var type = receiver.type();

				if (type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class)) {
					return BodyHandlers.discarding();
				}

				if (type.isAssignableFrom(InputStream.class)) {
					return BodyHandlers.ofInputStream();
				}

				return responseInfo -> BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8),
						json -> {
							if (responseInfo.statusCode() >= 300) {
								return json;
							}

							// TODO:
							final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE).orElse("")
									.toLowerCase();
							if (!contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
								throw new RuntimeException("Un-supported response content type:" + contentType);
							}

							return clientConfig.contentConsumer(req.accept()).consume(json, receiver);
						});
			}

			private BodyPublisher bodyPublisher(final Request req) {
				final var body = req.body();
				if (body == null) {
					return BodyPublishers.noBody();
				}

				final var contentProducer = clientConfig.contentProducer(req.contentType());
				if (contentProducer == null) {
					throw new RuntimeException("No content producer for " + req.contentType());
				}

				return BodyPublishers.ofString(contentProducer.produce(req::body));
			}

		};
	}

	private HttpRequest.Builder newRequestBuilder(final Request req) {
		final var builder = reqBuilderSupplier.get();

		// Content-Type
		builder.header(HttpUtils.CONTENT_TYPE,
				Optional.ofNullable(req.contentType()).orElse(HttpUtils.APPLICATION_JSON));

		// Accept
		builder.header(HttpUtils.ACCEPT, Optional.ofNullable(req.accept()).orElse(HttpUtils.APPLICATION_JSON));

		return builder;
	}
}
