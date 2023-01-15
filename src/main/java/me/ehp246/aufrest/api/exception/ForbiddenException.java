package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 403
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class ForbiddenException extends ClientErrorException {
    private static final long serialVersionUID = -5883732900105281741L;

    public ForbiddenException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 403) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }

}
