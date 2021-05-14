package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 * @since 2.3.7
 *
 */
public final class ClientErrorResponseException extends Exception {
    private static final long serialVersionUID = 3539564874094568554L;
    private final RestRequest request;
    private final HttpResponse<?> response;

    public ClientErrorResponseException(final RestRequest request, final HttpResponse<?> response) {
        super();
        if (response.statusCode() < 400 || response.statusCode() >= 500) {
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
