package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 400
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class BadRequestException extends ClientErrorException {
    private static final long serialVersionUID = -4314837155988010463L;

    public BadRequestException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 400) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }
}
