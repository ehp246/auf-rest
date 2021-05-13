package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public final class ServerErrorException extends Exception {
    private static final long serialVersionUID = -3503724512415848631L;

    private final RestRequest request;
    private final HttpResponse<?> response;

    public ServerErrorException(final RestRequest request, final HttpResponse<?> response) {
        super();
        if (response.statusCode() < 500) {
            throw new IllegalArgumentException();
        }

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
