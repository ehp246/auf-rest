package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.BodyFn;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ClientFnProvider;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RequestFilter;
import me.ehp246.aufrest.api.rest.RestResponse;
import me.ehp246.aufrest.api.rest.ResponseFilter;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.TextBodyFn;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * For each call to return a HTTP client, the provider should ask the
 * client-builder supplier for a new builder. For each HTTP request, the
 * provider should ask the request-builder supplier for a new builder. The
 * provider should not cache/re-use any builders.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 2.1.1
 */
public class JdkClientProvider implements ClientFnProvider {
	private final static Logger LOGGER = LogManager.getLogger(JdkClientProvider.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier;
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier;

	public JdkClientProvider() {
		this.clientBuilderSupplier = HttpClient::newBuilder;
		this.reqBuilderSupplier = HttpRequest::newBuilder;
	}

	public JdkClientProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier) {
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilderSupplier = HttpRequest::newBuilder;
	}

	public JdkClientProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier,
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
			private final List<RequestFilter> requestFilters = Collections
					.unmodifiableList(Optional.ofNullable(clientConfig.requestFilters()).orElseGet(ArrayList::new));
			private final List<ResponseFilter> responseFilters = Collections
					.unmodifiableList(Optional.ofNullable(clientConfig.responseFilters()).orElseGet(ArrayList::new));
			private final Optional<AuthorizationProvider> authProvider = Optional
					.ofNullable(clientConfig.authProvider());
			private final Optional<HeaderProvider> headerProvider = Optional.ofNullable(clientConfig.headerProvider());
			private final Set<BodyFn> bodyFns = Optional.ofNullable(clientConfig.bodyFns()).orElseGet(HashSet::new)
					.stream().collect(Collectors.toUnmodifiableSet());

			@SuppressWarnings("unchecked")
			@Override
			public RestResponse apply(final RestRequest req) {
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
				Optional.ofNullable(authHeader)
						.ifPresent(header -> requestBuilder.header(HttpUtils.AUTHORIZATION, header));

				// Applying filters
				var httpRequest = requestBuilder.build();
				for (final var filter : requestFilters) {
					LOGGER.atTrace().log("Applying request filter {}", filter.getClass().getName());
					httpRequest = filter.apply(httpRequest, req);
				}
				final var reqRef = new AtomicReference<>(httpRequest);

				HttpResponse<Object> httpResponse;
				try {
					httpResponse = (HttpResponse<Object>) client.send(httpRequest, bodyHandler(req));
				} catch (IOException | InterruptedException e) {
					LOGGER.atError().log("Failed to send request: " + e.getMessage(), e);
					throw new RuntimeException(e);
				}

				RestResponse responseByRest = new RestResponse() {

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
						return reqRef.get();
					}

				};

				for (final var filter : responseFilters) {
					LOGGER.atTrace().log("Applying response filter {}", filter.getClass().getName());
					responseByRest = filter.apply(responseByRest);
				}

				return responseByRest;
			}

			private BodyHandler<?> bodyHandler(final RestRequest request) {
				final var receiver = request.bodyReceiver();
				final Class<?> type = receiver == null ? void.class : receiver.type();

				if (type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class)) {
					return BodyHandlers.discarding();
				}

				return responseInfo -> {
					LOGGER.atDebug().log("Status: {}", responseInfo.statusCode());
					LOGGER.atTrace().log("Headers: {}", responseInfo.headers().map());

					// Default to UTF-8 text
					return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), text -> {
						LOGGER.atTrace().log("Body: {}", text);

						if (responseInfo.statusCode() >= 300) {
							return text;
						}

						final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE).get()
								.toLowerCase();

						final var reader = bodyFns.stream().filter(bodyFn -> bodyFn.accept(contentType)).findAny()
								.get();

						return ((TextBodyFn) reader).fromText(text, receiver);
					});
				};
			}

			private BodyPublisher bodyPublisher(final RestRequest req) {
				if (req.body() == null) {
					return BodyPublishers.noBody();
				}

				final var writer = bodyFns.stream().filter(bodyFn -> bodyFn.accept((req.contentType().toLowerCase())))
						.findAny().get();
				if (writer == null || !(writer instanceof TextBodyFn)) {
					throw new RuntimeException("No content producer for " + req.contentType());
				}

				return BodyPublishers.ofString(((TextBodyFn) writer).toText(req::body));
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
