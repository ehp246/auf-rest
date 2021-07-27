package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * An {@link ErrorResponseException} that has a status code in the range of 400
 * and 499.
 * 
 * @author Lei Yang
 * @since 2.3.7
 *
 */
public final class ClientErrorResponseException extends ErrorResponseException {
    private static final long serialVersionUID = 3539564874094568554L;

    public ClientErrorResponseException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode < 400 || response.statusCode() > 499) {
            throw new IllegalArgumentException("Un-supported status code: " + statusCode);
        }
    }
}
