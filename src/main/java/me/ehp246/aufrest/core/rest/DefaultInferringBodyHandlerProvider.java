package me.ehp246.aufrest.core.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.springframework.lang.Nullable;

import me.ehp246.aufrest.api.rest.BodyHandlerType;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Implementation of the bean of {@linkplain InferringBodyHandlerProvider}.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 4.0
 * @see AufRestConfiguration
 */
final class DefaultInferringBodyHandlerProvider implements InferringBodyHandlerProvider {
    private final FromJson fromJson;
    private final RestLogger restLogger;

    public DefaultInferringBodyHandlerProvider(final FromJson jsonFn, @Nullable final RestLogger restLogger) {
        super();
        this.fromJson = jsonFn;
        this.restLogger = restLogger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> BodyHandler<T> get(final BodyHandlerType.Inferring<T> handler) {

        final var successPayloadDescriptor = handler;
        // Needed for both provided and inferring descriptors.
        final var errorPayloadDescriptor = handler == null ? null
                : JacksonTypeDescriptor.of(handler.errorType(), null);

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

            final var isSuccess = HttpUtils.isSuccess(statusCode);

            /*
             * The descriptor for this particular response. By this time, the descriptor
             * could be for either a success or a failure. Can be null. In which case, try
             * to infer by the content type.
             */
            final var resposnePayloadDescriptor = isSuccess ? successPayloadDescriptor : errorPayloadDescriptor;
            final var responseReturnType = resposnePayloadDescriptor == null ? null : resposnePayloadDescriptor.type();
            if (statusCode == 204 || responseReturnType == void.class || responseReturnType == Void.class) {
                return BodySubscribers.mapping(BodySubscribers.discarding(), v -> null);
            }

            // Short-circuit the content-type.
            if (responseReturnType == InputStream.class) {
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
                        if (responseReturnType == String.class) {
                            return text;
                        }

                        if (contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
                            return fromJson.fromJson(text, resposnePayloadDescriptor);
                        }

                        // Returns the raw text for anything that is not JSON for now.
                        return text;
                    });
        };
    }

}
