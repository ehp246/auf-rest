package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class RedirectionException extends Exception {
    private static final long serialVersionUID = -397837195245058784L;

    private final RestRequest request;
    private final HttpResponse<?> response;

    public RedirectionException(RestRequest request, HttpResponse<?> response) {
        super();
        this.request = request;
        this.response = response;
    }

    public HttpResponse<?> httpResponse() {
        return this.response;
    }

    public int statusCode() {
        return this.httpResponse().statusCode();
    }

    public RestRequest request() {
        return request;
    }

}
