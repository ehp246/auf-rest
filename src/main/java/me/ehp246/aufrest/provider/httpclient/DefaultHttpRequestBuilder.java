package me.ehp246.aufrest.provider.httpclient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.ContentPublisherProvider;
import me.ehp246.aufrest.api.rest.ContentPublisherProvider.ContentPublisher;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.AufRestConfiguration;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Builds a {@linkplain HttpRequest} from a {@linkplain RestRequest}.
 * <p>
 * Available as Spring bean.
 *
 * @author Lei Yang
 * @since 1.0
 * @see AufRestConfiguration
 */
public final class DefaultHttpRequestBuilder implements HttpRequestBuilder {
    private final Supplier<HttpRequest.Builder> reqBuilderSupplier;
    private final ContentPublisherProvider publisherProvider;
    private final Optional<HeaderProvider> headerProvider;
    private final Optional<AuthProvider> authProvider;
    private final Duration responseTimeout;

    public DefaultHttpRequestBuilder(final Supplier<HttpRequest.Builder> reqBuilderSupplier,
            final HeaderProvider headerProvider, final AuthProvider authProvider,
            final ContentPublisherProvider publisherProvider, final String requestTimeout) {
        super();
        this.reqBuilderSupplier = reqBuilderSupplier == null ? HttpRequest::newBuilder : reqBuilderSupplier;
        this.headerProvider = Optional.ofNullable(headerProvider);
        this.authProvider = Optional.ofNullable(authProvider);
        this.publisherProvider = publisherProvider;
        this.responseTimeout = Optional.ofNullable(requestTimeout).filter(OneUtil::hasValue)
                .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                        e -> new IllegalArgumentException(AufRestConstants.RESPONSE_TIMEOUT + ": " + value)))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HttpRequest apply(final RestRequest req, final RestBodyDescriptor<?> descriptor) {
        final var builder = reqBuilderSupplier.get();
        final var providedHeaders = headerProvider.map(provider -> provider.get(req)).orElseGet(HashMap::new);
        /*
         * Provider headers, context headers, request headers in ascending priority.
         */
        Optional.ofNullable(Stream
                .of(providedHeaders, HeaderContext.map(), Optional.ofNullable(req.headers()).orElseGet(HashMap::new))
                .map(Map::entrySet).flatMap(Set::stream).collect(Collectors
                        .toMap(entry -> entry.getKey().toLowerCase(Locale.US), Map.Entry::getValue, (o, n) -> n)))
                .map(Map::entrySet).stream().flatMap(Set::stream).forEach(entry -> {
                    final var key = entry.getKey();
                    final var values = entry.getValue();
                    if (HttpUtils.RESERVED_HEADERS.contains(key) || values == null || values.isEmpty()) {
                        return;
                    }
                    entry.getValue().stream().filter(OneUtil::hasValue).forEach(value -> builder.header(key, value));
                });

        /**
         * Required headers. Null and blank not allowed.
         *
         * accept, accept-encoding
         */
        builder.setHeader(HttpUtils.ACCEPT, Optional.of(req.accept()).filter(OneUtil::hasValue).get());
        Optional.ofNullable(req.acceptEncoding()).filter(OneUtil::hasValue)
                .ifPresent(value -> builder.setHeader(HttpUtils.ACCEPT_ENCODING, value));

        /*
         * Authentication in descending priority
         */
        if (req.authSupplier() != null) {
            Optional.ofNullable(req.authSupplier().get()).map(Object::toString).filter(OneUtil::hasValue)
                    .ifPresent(value -> builder.setHeader(HttpUtils.AUTHORIZATION, value));
        } else if (providedHeaders.get(HttpUtils.AUTHORIZATION) != null
                && providedHeaders.get(HttpUtils.AUTHORIZATION).size() > 0) {
            Optional.ofNullable(providedHeaders.get(HttpUtils.AUTHORIZATION).get(0)).filter(OneUtil::hasValue)
                    .ifPresent(value -> builder.setHeader(HttpUtils.AUTHORIZATION, value));
        } else if (authProvider.isPresent()) {
            authProvider.map(provider -> provider.get(req)).filter(OneUtil::hasValue)
                    .ifPresent(value -> builder.setHeader(HttpUtils.AUTHORIZATION, value));
        } else if (HeaderContext.map().getOrDefault(HttpUtils.AUTHORIZATION, List.of()).size() > 0) {
            Optional.ofNullable(HeaderContext.map().get(HttpUtils.AUTHORIZATION).get(0)).filter(OneUtil::hasValue)
                    .ifPresent(value -> builder.setHeader(HttpUtils.AUTHORIZATION, value));
        }

        /*
         * Timeout
         */
        Optional.ofNullable(req.timeout() == null ? responseTimeout : req.timeout())
                .ifPresent(timeout -> builder.timeout(timeout));

        /*
         * URI
         */
        final URI uri;
        if (req.contentType().equalsIgnoreCase(HttpUtils.APPLICATION_FORM_URLENCODED)) {
            // Expecting this uri doesn't have query parameters on it.
            uri = URI.create(req.uri());
        } else {
            // Add query parameters
            uri = URI.create(String.join("?", req.uri(),
                    HttpUtils.toQueryString(Optional.ofNullable(req.queries()).orElseGet(Map::of))));
        }

        /*
         * Body
         */
        final ContentPublisherProvider.ContentPublisher contentPublisher;
        if (req.contentType().equalsIgnoreCase(HttpUtils.APPLICATION_FORM_URLENCODED)) {
            // Encode query parameters as the body ignoring the body object.
            contentPublisher = new ContentPublisher(req.contentType(),
                    BodyPublishers.ofString(OneUtil.formUrlEncodedBody(req.queries())));
        } else {
            contentPublisher = this.publisherProvider.get(req.body(), req.contentType(),
                    (RestBodyDescriptor<Object>) descriptor);
        }

        builder.setHeader(HttpUtils.CONTENT_TYPE, contentPublisher.contentType());

        builder.method(req.method().toUpperCase(), contentPublisher.publisher()).uri(uri);

        return builder.build();
    }
}
