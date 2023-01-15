package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 401
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class NotAuthorizedException extends ClientErrorException {
    private static final long serialVersionUID = 4581705585370447700L;

    public NotAuthorizedException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 401) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }

}
