package me.ehp246.aufrest.core.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import org.springframework.lang.Nullable;

import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor.Inferring;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class DefaultBodyHandlerProvider implements InferringBodyHandlerProvider {
    private final FromJson fromJson;
    private final RestLogger restLogger;

    public DefaultBodyHandlerProvider(final FromJson jsonFn, @Nullable final RestLogger restLogger) {
        super();
        this.fromJson = jsonFn;
        this.restLogger = restLogger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> BodyHandler<T> get(final RestResponseDescriptor<T> descriptor) {
        Objects.nonNull(descriptor);

        // In case of provided handler, the success body type is not used and
        // irrelevant.
        final var successDescriptor = descriptor instanceof final Inferring<?> i ? i.body() : null;
        // Needed for both provided and inferring descriptors.
        final var errorDescriptor = new RestBodyDescriptor<>(descriptor.errorType());

        return responseInfo -> {
            // Log headers
            if (restLogger != null) {
                this.restLogger.onResponseInfo(responseInfo);
            }

            final var statusCode = responseInfo.statusCode();
            final var gzipped = responseInfo.headers().firstValue(HttpUtils.CONTENT_ENCODING).orElse("")
                    .equalsIgnoreCase("gzip");
            // The server might not set the header. If not, assume JSON. Otherwise, follow
            // the header.
            final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE)
                    .orElse(HttpUtils.APPLICATION_JSON);

            final RestBodyDescriptor<?> resposneDescriptor;
            if (statusCode >= 200 && statusCode < 300) {
                // Use the supplied if it is supplied.
                if (descriptor instanceof final RestResponseDescriptor.Provided<?> handleSupplier) {
                    return (BodySubscriber<T>) handleSupplier.handler();
                }
                resposneDescriptor = successDescriptor;
            } else {
                resposneDescriptor = errorDescriptor;
            }

            // By this time, the descriptor could be for either a success or a failure.
            final var type = resposneDescriptor.type();
            final var dischardBody = type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class);
            if (statusCode == 204 || dischardBody) {
                return BodySubscribers.mapping(BodySubscribers.discarding(), v -> null);
            }

            // Short-circuit the content-type.
            if (type.isAssignableFrom(InputStream.class)) {
                // Wrap it in a gzip stream.
                return gzipped
                        ? BodySubscribers.mapping(BodySubscribers.ofInputStream(),
                                in -> OneUtil.orThrow(() -> (T) new GZIPInputStream(in)))
                        : BodySubscribers.mapping(BodySubscribers.ofInputStream(), t -> (T) t);
            }

            return (BodySubscriber<T>) BodySubscribers
                    .mapping(gzipped ? BodySubscribers.mapping(BodySubscribers.ofByteArray(), bytes -> {
                        try (final var gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
                                final var byteOs = new ByteArrayOutputStream()) {
                            gis.transferTo(byteOs);
                            return byteOs.toString(StandardCharsets.UTF_8);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }) : BodySubscribers.ofString(StandardCharsets.UTF_8), text -> {
                        if (restLogger != null) {
                            restLogger.onResponseBody(text);
                        }

                        // This means a JSON string will not be de-serialized.
                        if (type.isAssignableFrom(String.class)) {
                            return text;
                        }

                        if (contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
                            return fromJson.apply(text, resposneDescriptor);
                        }

                        // Returns the raw text for anything that is not JSON for now.
                        return text;
                    });
        };
    }

}
