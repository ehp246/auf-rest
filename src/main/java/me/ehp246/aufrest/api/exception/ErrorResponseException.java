package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Thrown when the library receives a HTTP response that it dosn't know how to
 * handle. The exact occasions of the exception depend on the method signature
 * of the {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interface.
 * <p>
 * The exception happens only after a response has been received.
 * 
 * @author Lei Yang
 * @since 2.5.0
 *
 */
public class ErrorResponseException extends Exception {
    private static final long serialVersionUID = -182048232082907551L;

    protected final RestRequest request;
    protected final HttpResponse<?> response;

    public ErrorResponseException(final RestRequest request, final HttpResponse<?> response) {
        super(response.request().method() + " " + response.uri().toString() + " " + response.statusCode()
                + System.lineSeparator() + OneUtil.toString(response.body()));
        this.request = request;
        this.response = response;
    }

    public HttpResponse<?> httpResponse() {
        return this.response;
    }

    public int statusCode() {
        return this.httpResponse().statusCode();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T responseBody(Class<T> t) {
        return (T) this.response.body();
    }

    public Object responseBody() {
        return this.response.body();
    }

    public String bodyToString() {
        return OneUtil.toString(this.httpResponse().body());
    }

    public RestRequest request() {
        return request;
    }

}