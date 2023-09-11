package me.ehp246.aufrest.api.exception;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Wraps any checked exceptions raised during
 * {@linkplain RestFn#applyForResponse(me.ehp246.aufrest.api.rest.RestRequest, me.ehp246.aufrest.api.rest.BodyOf, me.ehp246.aufrest.api.rest.BodyHandlerType)}
 * operation before a {@linkplain HttpResponse} is received/created.
 * <p>
 * It covers and only covers checked exceptions during the send/receive
 * operation. What happens after the response is received is covered by
 * {@linkplain ErrorResponseException}.
 *
 * @author Lei Yang
 * @see HttpClient#send(java.net.http.HttpRequest,
 *      java.net.http.HttpResponse.BodyHandler)
 */
public final class RestFnException extends RuntimeException {
    private static final long serialVersionUID = 7172740406607274087L;

    private final RestRequest request;
    private final HttpRequest httpRequest;
    private final String message;

    public RestFnException(final RestRequest request, final HttpRequest httpRequest, final Exception e) {
        super(e);
        this.request = request;
        this.httpRequest = httpRequest;
        this.message = httpRequest.method() + " " + httpRequest.uri() + " failed because of " + e.toString();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public RestRequest getRestRequest() {
        return this.request;
    }

    public HttpRequest getHttpRequest() {
        return this.httpRequest;
    }
}
