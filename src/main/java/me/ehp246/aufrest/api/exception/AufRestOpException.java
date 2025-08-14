package me.ehp246.aufrest.api.exception;

import java.net.http.HttpClient;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Wraps any checked exceptions raised during
 * {@linkplain RestFn#applyForResponse(RestRequest)} operation.
 *
 * @author Lei Yang
 * @see HttpClient#send(java.net.http.HttpRequest,
 *      java.net.http.HttpResponse.BodyHandler)
 */
public final class AufRestOpException extends RuntimeException {
    private static final long serialVersionUID = 7172740406607274087L;

    public AufRestOpException(final Exception cause) {
        super(cause);
    }

    public AufRestOpException(final String message, final Exception cause) {
        super(message, cause);
    }
}
