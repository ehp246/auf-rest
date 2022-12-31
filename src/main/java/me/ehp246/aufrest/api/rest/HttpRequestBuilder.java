package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

/**
 * The abstraction that can build a {@link HttpRequest} from a
 * {@link RestRequest}.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface HttpRequestBuilder {
    HttpRequest apply(RestRequest req);
}
