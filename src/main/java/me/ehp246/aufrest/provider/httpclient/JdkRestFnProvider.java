package me.ehp246.aufrest.provider.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BodyPublisherProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestObserver;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestResponse;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * For each call to return a HTTP client, the provider should ask the
 * client-builder supplier for a new builder. For each HTTP request, the
 * provider should ask the request-builder supplier for a new builder. The
 * provider should not cache/re-use any builders.
 *
 * @author Lei Yang
 */
public class JdkRestFnProvider implements RestFnProvider {
	private final static Logger LOGGER = LogManager.getLogger(JdkRestFnProvider.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier;
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier;

	public JdkRestFnProvider() {
		this.clientBuilderSupplier = HttpClient::newBuilder;
		this.reqBuilderSupplier = HttpRequest::newBuilder;
	}

	public JdkRestFnProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier) {
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilderSupplier = HttpRequest::newBuilder;
	}

	public JdkRestFnProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier,
			final Supplier<HttpRequest.Builder> reqBuilderSupplier) {
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilderSupplier = reqBuilderSupplier;
	}

	@Override
	public RestFn get(final ClientConfig clientConfig) {
		final var clientBuilder = clientBuilderSupplier.get();
		if (clientConfig.connectTimeout() != null) {
			clientBuilder.connectTimeout(clientConfig.connectTimeout());
		}

		return new RestFn() {
			private final HttpClient client = clientBuilder.build();
			private final List<RestObserver> observers = Collections
					.unmodifiableList(Optional.ofNullable(clientConfig.restObservers()).orElseGet(ArrayList::new));
			private final Optional<AuthProvider> authProvider = Optional
					.ofNullable(clientConfig.authProvider());
			private final Optional<HeaderProvider> headerProvider = Optional.ofNullable(clientConfig.headerProvider());
			private final BodyPublisherProvider bodyPublisherProvider = clientConfig.bodyPublisherProvider();
			private final BodyHandlerProvider bodyHandlerProvider = clientConfig.bodyHandlerProvider();


			@SuppressWarnings("unchecked")
			@Override
			public RestResponse apply(final RestRequest req) {
				final var authHeader = Optional
						.ofNullable(Optional.ofNullable(req.authSupplier())
								.orElse(() -> authProvider.map(provider -> provider.get(req.uri())).orElse(null)).get())
						.filter(value -> value != null && !value.isBlank()).orElse(null);

				final var requestBuilder = newRequestBuilder(req)
						.method(req.method().toUpperCase(), bodyPublisherProvider.get(req))
						.uri(URI.create(req.uri()));

				// Timeout
				Optional.ofNullable(req.timeout() == null ? clientConfig.responseTimeout() : req.timeout())
						.ifPresent(timeout -> requestBuilder.timeout(timeout));

				// Authentication
				Optional.ofNullable(authHeader)
						.ifPresent(header -> requestBuilder.header(HttpUtils.AUTHORIZATION, header));

				// Applying filters
				final var httpRequest = requestBuilder.build();

				// Applying request consumers
				observers.stream().forEach(consumer -> consumer.preSend(httpRequest, req));

				final HttpResponse<Object> httpResponse;
				try {
					httpResponse = (HttpResponse<Object>) client.send(httpRequest, bodyHandlerProvider.get(req));
				} catch (Exception e) {
					LOGGER.atError().log("Failed to send request: " + e.getMessage(), e);
					// Applying consumers
					observers.stream().forEach(consumer -> consumer.onException(e, httpRequest, req));

					// Always wrap into a RuntimeException
					throw new RuntimeException(e);
				}

				// Applying response consumers
				observers.stream().forEach(consumer -> consumer.postSend(httpResponse, req));

				return new RestResponse() {

					@Override
					public RestRequest restRequest() {
						return req;
					}

					@Override
					public HttpResponse<Object> httpResponse() {
						return httpResponse;
					}

					@Override
					public HttpRequest httpRequest() {
						return httpRequest;
					}

				};
			}

			private HttpRequest.Builder newRequestBuilder(final RestRequest req) {
				final var builder = reqBuilderSupplier.get();

				// Provider headers, context headers, request headers in ascending priorities.
				fillAppHeaders(builder, Stream
						.of(new HashMap<String, List<String>>(
								headerProvider.map(provider -> provider.get(req)).orElseGet(HashMap::new)),
								HeaderContext.map(), Optional.ofNullable(req.headers()).orElseGet(HashMap::new))
						.map(Map::entrySet).flatMap(Set::stream).collect(Collectors.toMap(
								entry -> entry.getKey().toLowerCase(), Map.Entry::getValue, (left, right) -> right)));

				/**
				 * Required headers. Null and blank not allowed.
				 */
				// Request id
				builder.setHeader(HttpUtils.REQUEST_ID,
						Optional.ofNullable(req.id()).orElseGet(() -> UUID.randomUUID().toString()));
				// Content-Type.
				builder.setHeader(HttpUtils.CONTENT_TYPE,
						Optional.of(req.contentType()).filter(OneUtil::hasValue).get());

				// Accept.
				builder.setHeader(HttpUtils.ACCEPT, Optional.of(req.accept()).filter(OneUtil::hasValue).get());

				return builder;
			}
		};
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
