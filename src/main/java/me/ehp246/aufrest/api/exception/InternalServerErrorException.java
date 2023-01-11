package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 500
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class InternalServerErrorException extends ServerErrorException {
    private static final long serialVersionUID = 2178348433906014074L;

    public InternalServerErrorException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode != 500) {
            throw new IllegalArgumentException("Un-supported status code: " + statusCode);
        }
    }

}
