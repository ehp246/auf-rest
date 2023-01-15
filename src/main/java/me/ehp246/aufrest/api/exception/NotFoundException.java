package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 404
 *
 * @author Lei Yang
 *
 */
public final class NotFoundException extends ClientErrorException {
    private static final long serialVersionUID = 8813150721215752379L;

    public NotFoundException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 404) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }

}
