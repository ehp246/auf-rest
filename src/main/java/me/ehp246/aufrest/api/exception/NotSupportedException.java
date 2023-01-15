package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 415
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class NotSupportedException extends ClientErrorException {
    private static final long serialVersionUID = 3158378489667969042L;

    public NotSupportedException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 415) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }

}
