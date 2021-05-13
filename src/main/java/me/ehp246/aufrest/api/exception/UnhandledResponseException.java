package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Thrown when the framework receives a HttpResponse that it dosn't know how to
 * handle. The exact occasions of the exception depend on the method signature
 * of the {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interface.
 *
 * <p>
 * The exception happens only after a response has been received and the
 * framework cann't handle it according to the method signature.
 *
 * @author Lei Yang
 * @since 1.0
 *
 */
public final class UnhandledResponseException extends RuntimeException {
    private static final long serialVersionUID = 3813318541456042414L;

    private final RestRequest request;
    private final HttpResponse<?> response;

    public UnhandledResponseException(final RestRequest request, final HttpResponse<?> response) {
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

    public String bodyAsString() {
        return this.httpResponse().body().toString();
    }

    public RestRequest request() {
        return request;
    }
}
