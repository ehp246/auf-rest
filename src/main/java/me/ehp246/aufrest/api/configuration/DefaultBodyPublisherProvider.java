package me.ehp246.aufrest.api.configuration;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;

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
        if (body != null && body instanceof BodyPublisher publisher) {
            return publisher;
        }

        if (body != null && body instanceof InputStream stream) {
            return BodyPublishers.ofInputStream(() -> stream);
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

}
