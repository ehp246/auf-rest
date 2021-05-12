package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 
 * @author Lei Yang
 * @since 2.2
 */
public interface RestResponse {
    /**
     * 
     * @return The {@link RestRequest} for which the response is received.
     */
    RestRequest restRequest();

    /**
     * The initiating HTTP request created from the {@link RestRequest} before sent
     * by the client. It could be different from the HTTP request returned by the
     * HTTP response.
     * 
     * @return The initiating HTTP request before sent.
     */
    HttpRequest httpRequest();

    /**
     * 
     * @return Received HTTP response
     */
    HttpResponse<?> httpResponse();
}
