package me.ehp246.aufrest.provider.httpclient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyPublisherProvider;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class DefaultRequestBuilder implements RequestBuilder {
	private final static Logger LOGGER = LogManager.getLogger(DefaultRequestBuilder.class);

	private final Supplier<HttpRequest.Builder> reqBuilderSupplier = HttpRequest::newBuilder;
	private final BodyPublisherProvider bodyPublisherProvider;
	private final Optional<HeaderProvider> headerProvider;
	private final Optional<AuthProvider> authProvider;
	private final Duration responseTimeout;

	public DefaultRequestBuilder(@Autowired(required = false) final HeaderProvider headerProvider) {
		this(headerProvider, null, req -> null, null);
	}

	@Autowired
	public DefaultRequestBuilder(@Autowired(required = false) final HeaderProvider headerProvider,
			@Autowired(required = false) final AuthProvider authProvider,
			final BodyPublisherProvider bodyPublisherProvider,
			@Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout) {
		super();
		this.headerProvider = Optional.ofNullable(headerProvider);
		this.authProvider = Optional.ofNullable(authProvider);
		this.bodyPublisherProvider = bodyPublisherProvider;
		this.responseTimeout = Optional.ofNullable(requestTimeout).filter(OneUtil::hasValue)
				.map(value -> OneUtil.orThrow(() -> Duration.parse(value),
						e -> new IllegalArgumentException(AufRestConstants.RESPONSE_TIMEOUT + ": " + value)))
				.orElse(null);
	}

	@Override
	public HttpRequest apply(RestRequest req) {
		final var builder = reqBuilderSupplier.get();

		// Provider headers, context headers, request headers in ascending priorities.
		fillAppHeaders(builder, Stream
				.of(new HashMap<String, List<String>>(
						headerProvider.map(provider -> provider.get(req)).orElseGet(HashMap::new)), HeaderContext.map(),
						Optional.ofNullable(req.headers()).orElseGet(HashMap::new))
				.map(Map::entrySet).flatMap(Set::stream).collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(),
						Map.Entry::getValue, (left, right) -> right)));

		/**
		 * Required headers. Null and blank not allowed.
		 */
		// Request id
		builder.setHeader(HttpUtils.REQUEST_ID,
				Optional.ofNullable(req.id()).orElseGet(() -> UUID.randomUUID().toString()));
		// Content-Type.
		builder.setHeader(HttpUtils.CONTENT_TYPE, Optional.of(req.contentType()).filter(OneUtil::hasValue).get());

		// Accept.
		builder.setHeader(HttpUtils.ACCEPT, Optional.of(req.accept()).filter(OneUtil::hasValue).get());

		final var authHeader = Optional
				.ofNullable(Optional.ofNullable(req.authSupplier())
						.orElse(() -> authProvider.map(provider -> provider.get(req)).orElse(null)).get())
				.filter(value -> value != null && !value.isBlank()).orElse(null);

		builder.method(req.method().toUpperCase(), bodyPublisherProvider.get(req)).uri(URI.create(req.uri()));

		// Timeout
		Optional.ofNullable(req.timeout() == null ? responseTimeout : req.timeout())
				.ifPresent(timeout -> builder.timeout(timeout));

		// Authentication
		Optional.ofNullable(authHeader).ifPresent(header -> builder.header(HttpUtils.AUTHORIZATION, header));

		return builder.build();
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
