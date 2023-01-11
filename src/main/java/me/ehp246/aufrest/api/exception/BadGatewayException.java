package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 502
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class BadGatewayException extends ServerErrorResponseException {
    private static final long serialVersionUID = 4026754453331838045L;

    public BadGatewayException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode != 502) {
            throw new IllegalArgumentException("Illegal status code: " + statusCode);
        }
    }
}
