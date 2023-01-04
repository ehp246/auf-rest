package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.configuration.AufRestConstants;
import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyDescriptor;
import me.ehp246.aufrest.api.rest.BodyDescriptor.JsonViewValue;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpRequestBuilder;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.ToJson;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Builds a {@linkplain HttpRequest} from a {@linkplain RestRequest}.
 *
 * @author Lei Yang
 *
 */
public final class DefaultHttpRequestBuilder implements HttpRequestBuilder {
    private final Supplier<HttpRequest.Builder> reqBuilderSupplier;
    private final ToJson toJson;
    private final Optional<HeaderProvider> headerProvider;
    private final Optional<AuthProvider> authProvider;
    private final Duration responseTimeout;

    public DefaultHttpRequestBuilder(@Nullable final Supplier<HttpRequest.Builder> reqBuilderSupplier,
            @Nullable final HeaderProvider headerProvider, @Nullable final AuthProvider authProvider,
            final ToJson toJson, @Nullable final String requestTimeout) {
        super();
        this.reqBuilderSupplier = reqBuilderSupplier == null ? HttpRequest::newBuilder : reqBuilderSupplier;
        this.headerProvider = Optional.ofNullable(headerProvider);
        this.authProvider = Optional.ofNullable(authProvider);
        this.toJson = toJson;
        this.responseTimeout = Optional.ofNullable(requestTimeout).filter(OneUtil::hasValue)
                .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                        e -> new IllegalArgumentException(AufRestConstants.RESPONSE_TIMEOUT + ": " + value)))
                .orElse(null);
    }

    @Override
    public HttpRequest apply(final RestRequest req, final BodyDescriptor descriptor) {
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
            uri = URI.create(UriComponentsBuilder.fromUriString(req.uri())
                    .queryParams(
                            CollectionUtils.toMultiValueMap(Optional.ofNullable(req.queries()).orElseGet(Map::of)))
                    .toUriString());
        }

        /*
         * Body
         */
        final var contentPublisher = getContentPublisher(req, descriptor);

        builder.setHeader(HttpUtils.CONTENT_TYPE, contentPublisher.contentType);

        builder.method(req.method().toUpperCase(), contentPublisher.publisher()).uri(uri);

        return builder.build();
    }

    private ContentPublisher getContentPublisher(final RestRequest req, final BodyDescriptor descriptor) {
        final var body = req.body();

        final var contentType = Optional.of(req.contentType()).filter(OneUtil::hasValue)
                .orElse(HttpUtils.APPLICATION_JSON);

        if (body instanceof final BodyPublisher publisher) {
            return new ContentPublisher(contentType, publisher);
        }

        if (contentType.equalsIgnoreCase(HttpUtils.APPLICATION_FORM_URLENCODED)) {
            // Encode query parameters as the body ignoring the body object.
            return new ContentPublisher(contentType,
                    BodyPublishers.ofString(OneUtil.formUrlEncodedBody(req.queries())));
        }

        if (body == null) {
            return new ContentPublisher(contentType, BodyPublishers.noBody());
        }

        // The rest requires a body.
        if (body instanceof final InputStream stream) {
            return new ContentPublisher(contentType, BodyPublishers.ofInputStream(() -> stream));
        }

        if (body instanceof final Path path) {
            final var boundry = new String(MimeTypeUtils.generateMultipartBoundary(), StandardCharsets.UTF_8);

            return new ContentPublisher(HttpUtils.MULTIPART_FORM_DATA + ";boundary=" + boundry,
                    ofMimeMultipartData(Map.of("file", path), boundry));
        }

        if (contentType.equalsIgnoreCase(HttpUtils.TEXT_PLAIN)) {
            return new ContentPublisher(contentType, BodyPublishers.ofString(body.toString()));
        }

        if (contentType.equalsIgnoreCase(HttpUtils.APPLICATION_JSON)) {
            // Must be a JSON object.
            return new ContentPublisher(contentType,
                    BodyPublishers.ofString(toJson.apply(body, (JsonViewValue) descriptor)));
        }

        throw new IllegalArgumentException("Un-supported content type '" + contentType + "' and object '"
                + body.toString() + "' of type '" + descriptor.type() + "'");
    }

    private BodyPublisher ofMimeMultipartData(final Map<Object, Object> data, final String boundary) {
        final var byteArrays = new ArrayList<byte[]>();
        final byte[] separator = ("--" + boundary + "\r\ncontent-disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);

        try {
            for (final Map.Entry<Object, Object> entry : data.entrySet()) {
                byteArrays.add(separator);

                final var key = entry.getKey();
                final var value = entry.getValue();
                if (value instanceof final Path path) {
                    final var mimeType = Files.probeContentType(path);

                    byteArrays.add(("\"" + key + "\"; filename=\"" + path.getFileName() + "\"\r\ncontent-type: "
                            + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                    byteArrays.add(Files.readAllBytes(path));
                    byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
                } else {
                    byteArrays.add(("\"" + key + "\"\r\n\r\n" + value + "\r\n").getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (final IOException e) {
            throw new RestFnException(e);
        }

        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return BodyPublishers.ofByteArrays(byteArrays);
    }

    private record ContentPublisher(String contentType, BodyPublisher publisher) {
    }
}
