package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Status code 406
 *
 * @author Lei Yang
 *
 */
public final class NotAcceptableException extends ClientErrorException {
    private static final long serialVersionUID = 4477877536865788150L;

    public NotAcceptableException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        if (response.statusCode() != 406) {
            throw new IllegalArgumentException("Un-supported status code: " + response.statusCode());
        }
    }

}
