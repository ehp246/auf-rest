package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

/**
 * The abstraction that can build a {@link HttpRequest} from a
 * {@link RestRequest}.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface HttpRequestBuilder {
    HttpRequest apply(RestRequest req, BodyDescriptor descriptor);

    default HttpRequest apply(final RestRequest req) {
        return this.apply(req, req.body() == null ? null : new BodyDescriptor(req.body().getClass(), null));
    }
}
