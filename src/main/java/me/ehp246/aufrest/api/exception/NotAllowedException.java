package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 405
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class NotAllowedException extends ClientErrorException {
    private static final long serialVersionUID = -4658901163298208850L;

    public NotAllowedException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 405) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }

}
