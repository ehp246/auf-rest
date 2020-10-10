package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.Request;
import me.ehp246.aufrest.core.util.OneUtil;

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

	public JdkClientProvider(final HeaderProvider headerProvider) {
		this(HttpClient::newBuilder, HttpRequest::newBuilder, new ClientConfig() {
		}, null, headerProvider);
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
				final var authHeader = Optional
						.ofNullable(Optional.ofNullable(req.authSupplier())
								.orElse(() -> authProvider.map(provider -> provider.get(req.uri())).orElse(null)).get())
						.filter(value -> value != null && !value.isBlank()).orElse(null);

				final var requestBuilder = newRequestBuilder(req).method(req.method().toUpperCase(), bodyPublisher(req))
						.uri(URI.create(req.uri()));

				// Timeout
				Optional.ofNullable(req.timeout() == null ? clientConfig.responseTimeout() : req.timeout())
						.ifPresent(timeout -> requestBuilder.timeout(timeout));

				// Authentication
				Optional.ofNullable(authHeader).map(header -> requestBuilder.header(HttpUtils.AUTHORIZATION, header));

				final var httpRequest = requestBuilder.build();
				LOGGER.atDebug().log("Request: {} {}", httpRequest.method(), req.uri());
				LOGGER.atTrace().log("Headers:{}", httpRequest.headers().map());

				HttpResponse<?> httpResponse;
				try {
					httpResponse = client.send(httpRequest, bodyHandler(req));
				} catch (IOException | InterruptedException e) {
					LOGGER.atError().log("Failed to send request: " + e.getMessage(), e);
					throw new RuntimeException(e);
				}

				return httpResponse;
			}

			private BodyHandler<?> bodyHandler(final Request req) {
				final var receiver = req.bodyReceiver();
				final Class<?> type = receiver == null ? void.class : receiver.type();

				if (type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class)) {
					return BodyHandlers.discarding();
				}

				return responseInfo -> {
					LOGGER.atDebug().log("Response status: {}", responseInfo.statusCode());
					LOGGER.atTrace().log("Headers: {}", responseInfo.headers().map());

					return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), json -> {
						LOGGER.atTrace().log("Body: {}", json);

						if (responseInfo.statusCode() >= 300
								|| OneUtil.isPresent(receiver.annotations(), AsIs.class)) {
							return json;
						}

						final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE).orElse("")
								.toLowerCase();
						if (!contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
							throw new RuntimeException();
						}
						return clientConfig.contentConsumer(req.accept()).consume(json, receiver);
					});
				};
			}

			private BodyPublisher bodyPublisher(final Request req) {
				if (req.body() == null) {
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

		// Provider headers, context headers, request headers in ascending priorities.
		fillAppHeaders(builder, Stream
				.of(new HashMap<String, List<String>>(
						headerProvider.map(provider -> provider.get(req)).orElseGet(HashMap::new)), HeaderContext.map(),
						Optional.ofNullable(req.headers()).orElseGet(HashMap::new))
				.map(Map::entrySet).flatMap(Set::stream).collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(),
						Map.Entry::getValue, (left, right) -> right)));

		// Content-Type
		builder.setHeader(HttpUtils.CONTENT_TYPE,
				Optional.ofNullable(req.contentType()).orElse(HttpUtils.APPLICATION_JSON));

		// Accept
		builder.setHeader(HttpUtils.ACCEPT, Optional.ofNullable(req.accept()).orElse(HttpUtils.APPLICATION_JSON));

		return builder;
	}

	/**
	 * Fill application-provided headers with reserved names filtering.
	 *
	 * @param builder
	 * @param headers
	 */
	private static void fillAppHeaders(final HttpRequest.Builder builder, final Map<String, List<String>> headers) {
		Optional.ofNullable(headers).map(Map::entrySet).stream().flatMap(Set::stream).forEach(entry -> {
			final var key = entry.getKey().toLowerCase(Locale.US);
			final var values = entry.getValue();
			if (HttpUtils.RESERVED_HEADERS.contains(key)) {
				LOGGER.atWarn().log("Ignoring header {}: {}", key, values);
				return;
			}
			if (values == null || values.isEmpty()) {
				return;
			}
			entry.getValue().stream().filter(value -> value != null && !value.isBlank())
					.forEach(value -> builder.header(key, value));
		});
	}
}
