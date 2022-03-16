package me.ehp246.aufrest.api.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.util.MimeTypeUtils;

import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.BodyPublisherProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.JsonFn;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class DefaultBodyPublisherProvider implements BodyPublisherProvider {
    private final JsonFn jsonFn;

    public DefaultBodyPublisherProvider(JsonFn jsonFn) {
        super();
        this.jsonFn = jsonFn;
    }

    @Override
    public BodyPublisher get(RestRequest req) {
        final var body = req.body();

        // Short-circuit for a few low-level types.
        // In these cases, the content type is ignored.
        if (body instanceof BodyPublisher publisher) {
            return publisher;
        }

        if (body instanceof InputStream stream) {
            return BodyPublishers.ofInputStream(() -> stream);
        }

        if (body instanceof Path file) {
            return ofMimeMultipartData(Map.of("", file));
        }

        // The rest needs the content type. No content type, no content.
        if (!OneUtil.hasValue(req.contentType())) {
            return BodyPublishers.noBody();
        }

        final var contentType = req.contentType().toLowerCase();

        if (contentType.equalsIgnoreCase(HttpUtils.APPLICATION_FORM_URLENCODED)) {
            // Encode query parameters as the body ignoring the body object.
            return BodyPublishers.ofString(OneUtil.formUrlEncodedBody(req.queryParams()));
        }

        if (body == null) {
            return BodyPublishers.noBody();
        }

        if (contentType.equalsIgnoreCase(HttpUtils.TEXT_PLAIN)) {
            return BodyPublishers.ofString(body.toString());
        }

        // Default to JSON.
        return BodyPublishers.ofString(jsonFn.toJson(body));
    }

    private static BodyPublisher ofMimeMultipartData(final Map<Object, Object> data) {
        final var boundary = new String(MimeTypeUtils.generateMultipartBoundary(), StandardCharsets.UTF_8);
        final var byteArrays = new ArrayList<byte[]>();
        final byte[] separator = ("--" + boundary + "\r\ncontent-disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);

        try {
            for (Map.Entry<Object, Object> entry : data.entrySet()) {
                byteArrays.add(separator);

                final var key = entry.getKey();
                final var value = entry.getValue();
                if (value instanceof Path path) {
                    final var mimeType = Files.probeContentType(path);

                    byteArrays.add(("\"" + key + "\"; filename=\"" + path.getFileName()
                            + "\"\r\ncontent-type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                    byteArrays.add(Files.readAllBytes(path));
                    byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
                } else {
                    byteArrays.add(("\"" + key + "\"\r\n\r\n" + value + "\r\n")
                            .getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            throw new RestFnException(e);
        }

        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return BodyPublishers.ofByteArrays(byteArrays);
    }
}
