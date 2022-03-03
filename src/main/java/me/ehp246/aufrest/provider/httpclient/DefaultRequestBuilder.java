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
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final Supplier<HttpRequest.Builder> reqBuilderSupplier;
    private final BodyPublisherProvider bodyPublisherProvider;
    private final Optional<HeaderProvider> headerProvider;
    private final Optional<AuthProvider> authProvider;
    private final Duration responseTimeout;

    public DefaultRequestBuilder(@Nullable final Supplier<HttpRequest.Builder> reqBuilderSupplier,
            @Nullable final HeaderProvider headerProvider, @Nullable final AuthProvider authProvider,
            @Nullable final BodyPublisherProvider bodyPublisherProvider, @Nullable final String requestTimeout) {
        super();
        this.reqBuilderSupplier = reqBuilderSupplier == null ? HttpRequest::newBuilder : reqBuilderSupplier;
        this.headerProvider = Optional.ofNullable(headerProvider);
        this.authProvider = Optional.ofNullable(authProvider);
        this.bodyPublisherProvider = bodyPublisherProvider == null ? req -> BodyPublishers.noBody()
                : bodyPublisherProvider;
        this.responseTimeout = Optional.ofNullable(requestTimeout).filter(OneUtil::hasValue)
                .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                        e -> new IllegalArgumentException(AufRestConstants.RESPONSE_TIMEOUT + ": " + value)))
                .orElse(null);
    }

    @Override
    public HttpRequest apply(RestRequest req) {
        final var builder = reqBuilderSupplier.get();
        final var providedHeaders = headerProvider.map(provider -> provider.get(req)).orElseGet(HashMap::new);
        // Provider headers, context headers, request headers in ascending priority.
        Optional.ofNullable(Stream
                .of(providedHeaders, HeaderContext.map(), Optional.ofNullable(req.headers()).orElseGet(HashMap::new))
                .map(Map::entrySet).flatMap(Set::stream).collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(),
                        Map.Entry::getValue, (left, right) -> right)))
                .map(Map::entrySet).stream().flatMap(Set::stream).forEach(entry -> {
                    final var key = entry.getKey().toLowerCase(Locale.US);
                    final var values = entry.getValue();
                    if (HttpUtils.RESERVED_HEADERS.contains(key) || values == null || values.isEmpty()) {
                        return;
                    }
                    entry.getValue().stream().filter(OneUtil::hasValue).forEach(value -> builder.header(key, value));
                });

        /**
         * Required headers. Null and blank not allowed.
         */
        // Request id
        builder.setHeader(HttpUtils.REQUEST_ID,
                Optional.ofNullable(req.id()).orElseGet(() -> UUID.randomUUID().toString()));

        // Content type
        builder.setHeader(HttpUtils.CONTENT_TYPE, Optional.of(req.contentType()).filter(OneUtil::hasValue).get());

        final URI uri;
        if (req.contentType().equalsIgnoreCase(HttpUtils.APPLICATION_FORM_URLENCODED)) {
            // Expecting this uri doesn't have query parameters on it.
            uri = URI.create(req.uri());
        } else {
            // Add query parameters
            uri = URI.create(UriComponentsBuilder.fromUriString(req.uri())
                    .queryParams(
                            CollectionUtils.toMultiValueMap(Optional.ofNullable(req.queryParams()).orElseGet(Map::of)))
                    .toUriString());
        }

        // Accept
        builder.setHeader(HttpUtils.ACCEPT, Optional.of(req.accept()).filter(OneUtil::hasValue).get());

        // Authentication in descending priority
        if (req.authSupplier() != null) {
            Optional.ofNullable(req.authSupplier().get()).filter(OneUtil::hasValue)
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

        // Timeout
        Optional.ofNullable(req.timeout() == null ? responseTimeout : req.timeout())
                .ifPresent(timeout -> builder.timeout(timeout));

        builder.method(req.method().toUpperCase(), bodyPublisherProvider.get(req)).uri(uri);

        return builder.build();
    }
}
