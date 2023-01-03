package me.ehp246.aufrest.api.exception;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Thrown when the proxy receives a HTTP response that it dosn't know how to
 * handle. The exact occasions of the exception depend on the method signature
 * of the {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interface.
 * <p>
 * The exception happens only after a {@linkplain HttpResponse} has been
 * received successfully. It doesn't cover anything prior.
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
    public <T> T body(final Class<T> t) {
        return (T) this.response.body();
    }

    public Object body() {
        return this.response.body();
    }

    public HttpHeaders headers() {
        return this.response.headers();
    }

    public List<String> headerValues(final String name) {
        return this.response.headers().allValues(name);
    }

    /**
     * Returns the first value of the header if present. Otherwise, returns the
     * <code>def</code>.
     *
     * @param name header name
     * @param def  the value to return if the response has no value for the header
     * @return header value or <code>def</code>
     */
    public String headerValue(final String name, final String def) {
        return this.response.headers().firstValue(name).orElse(def);
    }

    /**
     * Returns the first value of the header if present. Otherwise, returns
     * <code>null</code>.
     *
     * @param name header name
     * @return header value or <code>null</code>
     */
    public String headerValue(final String name) {
        return this.response.headers().firstValue(name).orElse(null);
    }

    public Map<String, List<String>> headersMap() {
        return this.response.headers().map();
    }

    public String bodyToString() {
        return OneUtil.toString(this.httpResponse().body());
    }

    public RestRequest request() {
        return request;
    }

}