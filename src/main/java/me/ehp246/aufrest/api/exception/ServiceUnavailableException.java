package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 503.
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class ServiceUnavailableException extends ServerErrorResponseException {
    private static final long serialVersionUID = 221830441972791734L;

    public ServiceUnavailableException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode != 503) {
            throw new IllegalArgumentException("Un-supported status code: " + statusCode);
        }
    }
}
