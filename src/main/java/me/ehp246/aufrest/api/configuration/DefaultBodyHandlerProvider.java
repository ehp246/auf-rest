package me.ehp246.aufrest.api.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import me.ehp246.aufrest.api.rest.BindingDescriptor;
import me.ehp246.aufrest.api.rest.BindingBodyHandlerProvider;
import me.ehp246.aufrest.api.spi.FromJson;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class DefaultBodyHandlerProvider implements BindingBodyHandlerProvider {
    private final FromJson fromJson;

    public DefaultBodyHandlerProvider(FromJson jsonFn) {
        super();
        this.fromJson = jsonFn;
    }

    @Override
    public BodyHandler<?> get(final BindingDescriptor binding) {
        final Class<?> type = binding == null ? void.class : binding.type();

        // Declared return type requires de-serialization.
        return responseInfo -> {
            final var statusCode = responseInfo.statusCode();
            final var gzipped = responseInfo.headers().firstValue(HttpHeaders.CONTENT_ENCODING).orElse("")
                    .equalsIgnoreCase("gzip");
            // The server might not set the header. Assuming JSON. Otherwise, follow the
            // header.
            final var contentType = responseInfo.headers().firstValue(HttpHeaders.CONTENT_TYPE)
                    .orElse(MediaType.APPLICATION_JSON_VALUE);

            // Short-circuit the content-type.
            if (type.isAssignableFrom(InputStream.class)) {
                return gzipped
                        ? BodySubscribers.mapping(BodySubscribers.ofInputStream(),
                                in -> OneUtil.orThrow(() -> new GZIPInputStream(in)))
                        : BodySubscribers.mapping(BodySubscribers.ofInputStream(), Function.identity());
            }

            return BodySubscribers.mapping(gzipped ? BodySubscribers.mapping(BodySubscribers.ofByteArray(), bytes -> {
                try (final var gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
                        final var byteOs = new ByteArrayOutputStream()) {
                    gis.transferTo(byteOs);
                    return byteOs.toString(StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }) : BodySubscribers.ofString(StandardCharsets.UTF_8), text -> {
                if ((statusCode == 204) || (statusCode < 300
                        && (type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class)))) {
                    return null;
                }

                // This means a JSON string will not be de-serialized.
                if (statusCode >= 300 && binding.errorType() == String.class) {
                    return text;
                }

                if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                    return fromJson.apply(text,
                            statusCode < 300 ? binding : new BindingDescriptor(binding.errorType()));
                }

                // Returns the raw text for anything that is not JSON for now.
                return text;
            });
        };
    }

}
