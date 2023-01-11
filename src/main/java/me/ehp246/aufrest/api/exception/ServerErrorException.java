package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * An {@link ErrorResponseException} that has a status code in the range of 500
 * and 599.
 *
 * @author Lei Yang
 * @since 4.0
 *
 */
public sealed class ServerErrorException
        extends
        ErrorResponseException permits ServiceUnavailableException, BadGatewayException, GatewayTimeoutException, InternalServerErrorException {
    private static final long serialVersionUID = -3503724512415848631L;

    public ServerErrorException(final RestRequest request, final HttpResponse<?> response) {
        super(request, response);
        final var statusCode = response.statusCode();
        if (statusCode < 500 || response.statusCode() > 599) {
            throw new IllegalArgumentException("Un-supported status code: " + statusCode);
        }
    }
}
