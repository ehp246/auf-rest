package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Thrown when the framework receives a HttpResponse that it dosn't know how to
 * handle. The exact occasions of the exception depend on the method signature
 * of the {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interface.
 *
 * <p>
 * The exception happens only after a response has been received and the
 * framework cann't handle it according to the method signature.
 * 
 * @author Lei Yang
 * @since 2.4.3
 *
 */
public class ErrorResponseException extends Exception {
    private static final long serialVersionUID = -182048232082907551L;

    protected final RestRequest request;
    protected final HttpResponse<?> response;

    public ErrorResponseException(final RestRequest request, final HttpResponse<?> response) {
        super();
        this.request = request;
        this.response = response;
    }

    public HttpResponse<?> httpResponse() {
        return this.response;
    }

    public int statusCode() {
        return this.httpResponse().statusCode();
    }

    public String bodyToString() {
        return OneUtil.toString(this.httpResponse().body());
    }

    public RestRequest request() {
        return request;
    }

}