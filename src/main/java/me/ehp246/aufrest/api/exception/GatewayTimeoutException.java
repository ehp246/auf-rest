package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 504
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class GatewayTimeoutException extends ServerErrorResponseException {
    private static final long serialVersionUID = -697691903891275721L;

    public GatewayTimeoutException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode != 504) {
            throw new IllegalArgumentException("Un-supported status code: " + statusCode);
        }
    }

}
