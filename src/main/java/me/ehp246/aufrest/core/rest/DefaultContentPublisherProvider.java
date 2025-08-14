package me.ehp246.aufrest.core.rest;

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

import me.ehp246.aufrest.api.exception.AufRestException;
import me.ehp246.aufrest.api.rest.ContentPublisherProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 * @since 4.0
 */
public class DefaultContentPublisherProvider implements ContentPublisherProvider {
    private final ToJson toJson;

    public DefaultContentPublisherProvider(final ToJson toJson) {
        super();
        this.toJson = toJson;
    }

    @Override
    public <T> ContentPublisher get(final T body, final String mimeType, final JacksonTypeDescriptor descriptor) {
        final var contentType = OneUtil.hasValue(mimeType) ? mimeType : HttpUtils.APPLICATION_JSON;

        if (body == null) {
            return new ContentPublisher(contentType, BodyPublishers.noBody());
        }

        if (body instanceof final BodyPublisher publisher) {
            /*
             * Respect a provided body publisher as much as possible. In this case, respect
             * the provide content type as well.
             */
            return new ContentPublisher(contentType, publisher);
        }

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
            return new ContentPublisher(contentType, BodyPublishers.ofString(toJson.toJson(body, descriptor)));
        }

        throw new IllegalArgumentException(
                "Un-supported content type '" + contentType + "' and object '" + body.toString() + "'");
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
            throw new AufRestException(e);
        }

        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return BodyPublishers.ofByteArrays(byteArrays);
    }
}
