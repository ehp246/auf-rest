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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.ContextHeader;
import me.ehp246.aufrest.api.rest.HeaderProvider;
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
	private final static Logger LOGGER = LogManager.getLogger(JdkClientProvider.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier;
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier;
	private final Optional<AuthorizationProvider> authProvider;
	private final Optional<HeaderProvider> headerProvider;
	private final ClientConfig clientConfig;

	public JdkClientProvider(final ClientConfig clientConfig) {
		this(HttpClient::newBuilder, HttpRequest::newBuilder, clientConfig, null, null);
	}

	public JdkClientProvider(final ClientConfig clientConfig, final AuthorizationProvider authProvider) {
		this(HttpClient::newBuilder, HttpRequest::newBuilder, clientConfig, authProvider, null);
	}

	public JdkClientProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier,
			final Supplier<HttpRequest.Builder> requestBuilderSupplier, final ClientConfig clientConfig) {
		this(clientBuilderSupplier, requestBuilderSupplier, clientConfig, null, null);
	}

	public JdkClientProvider(final ClientConfig clientConfig, final AuthorizationProvider authProvider,
			final HeaderProvider headerProvider) {
		this(HttpClient::newBuilder, HttpRequest::newBuilder, clientConfig, authProvider, headerProvider);
	}

	public JdkClientProvider(final Supplier<Builder> clientBuilderSupplier,
			final Supplier<HttpRequest.Builder> reqBuilderSupplier, final ClientConfig clientConfig,
			final AuthorizationProvider authProvider, final HeaderProvider headerProvider) {
		super();
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilderSupplier = reqBuilderSupplier;
		this.authProvider = Optional.ofNullable(authProvider);
		this.headerProvider = Optional.ofNullable(headerProvider);
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

				final var authHeader = Optional
						.ofNullable(Optional.ofNullable(req.authSupplier())
								.orElse(() -> authProvider.map(provider -> provider.get(uri)).orElse(null)).get())
						.filter(value -> value != null && !value.isBlank()).orElse(null);

				final var requestBuilder = newRequestBuilder(req).method(req.method().toUpperCase(), bodyPublisher(req))
						.uri(uri);

				// Timeout
				Optional.ofNullable(req.timeout() == null ? clientConfig.responseTimeout() : req.timeout())
						.map(requestBuilder::timeout);

				// Authentication
				Optional.ofNullable(authHeader).map(header -> requestBuilder.header(HttpUtils.AUTHORIZATION, header));

				final var httpRequest = requestBuilder.build();
				LOGGER.debug("{} {}", httpRequest.method(), req.uri());
				LOGGER.trace("{}", httpRequest.headers().map());

				HttpResponse<?> httpResponse;
				try {
					httpResponse = client.send(httpRequest, bodyHandler(req));
				} catch (IOException | InterruptedException e) {
					throw new RuntimeException("Failed to receive response", e);
				}

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

				return responseInfo -> {
					LOGGER.debug("{}", responseInfo.statusCode());
					LOGGER.trace("{}", responseInfo.headers().map());

					return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), json -> {
						LOGGER.trace("{}", json);

						if (responseInfo.statusCode() >= 300) {
							return json;
						}

						final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE).orElse("")
								.toLowerCase();
						if (!contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
							throw new RuntimeException("Un-supported response content type:" + contentType);
						}

						return clientConfig.contentConsumer(req.accept()).consume(json, receiver);
					});
				};
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

		// Provider headers. At the least priority.
		final var headers = new HashMap<String, List<String>>(
				headerProvider.map(provider -> provider.get(req.uri())).orElse(new HashMap<>()));

		// Context headers next.
		headers.putAll(ContextHeader.copy());

		// Request headers overwrite all above.
		Optional.ofNullable(req.headers()).ifPresent(reqHeaders -> headers.putAll(reqHeaders));

		fillHeaders(builder, headers);

		// Content-Type
		builder.setHeader(HttpUtils.CONTENT_TYPE,
				Optional.ofNullable(req.contentType()).orElse(HttpUtils.APPLICATION_JSON));

		// Accept
		builder.setHeader(HttpUtils.ACCEPT, Optional.ofNullable(req.accept()).orElse(HttpUtils.APPLICATION_JSON));

		return builder;
	}

	private static void fillHeaders(final HttpRequest.Builder builder, final Map<String, List<String>> headers) {
		Optional.ofNullable(headers).map(Map::entrySet).stream().flatMap(Set::stream).forEach(entry -> {
			final var key = entry.getKey();
			final var values = entry.getValue();
			if (values == null || values.isEmpty()) {
				return;
			}
			entry.getValue().stream().filter(value -> value != null && !value.isBlank())
					.forEach(value -> builder.header(key, value));
		});
	}
}
