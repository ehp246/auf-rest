package me.ehp246.aufrest.core.rest;

import java.net.http.HttpRequest;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.provider.httpclient.DefaultHttpRequestBuilder;

/**
 * Internal abstraction that builds a {@link HttpRequest} from a
 * {@link RestRequest}.
 *
 * @author Lei Yang
 * @see DefaultHttpRequestBuilder
 * @since 4.0
 */
@FunctionalInterface
public interface HttpRequestBuilder {
    HttpRequest apply(RestRequest req);
}
