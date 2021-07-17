package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * An {@link ErrorResponseException} that has a status code in the range of 300
 * and 399.
 * 
 * @author Lei Yang
 * @since 2.3.7
 *
 */
public final class RedirectionResponseException extends ErrorResponseException {
    private static final long serialVersionUID = -397837195245058784L;

    public RedirectionResponseException(RestRequest request, HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode < 300 || response.statusCode() > 399) {
            throw new IllegalArgumentException("Un-supported status code: " + statusCode);
        }
    }
}
