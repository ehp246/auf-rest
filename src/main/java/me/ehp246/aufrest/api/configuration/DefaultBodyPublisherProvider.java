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
        if (req.body() == null) {
            return BodyPublishers.noBody();
        }

        // Short-circuit for a few low-level types.
        // In these cases, the content type is ignored.
        if (req.body() instanceof BodyPublisher body) {
            return body;
        }
        if (req.body() instanceof InputStream body) {
            return BodyPublishers.ofInputStream(() -> body);
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

        if (contentType.equalsIgnoreCase(HttpUtils.TEXT_PLAIN)) {
            return BodyPublishers.ofString(req.body().toString());
        }

        // Default to JSON.
        return BodyPublishers.ofString(jsonFn.toJson(req.body()));
    }

}
