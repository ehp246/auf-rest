package me.ehp246.aufrest.api.exception;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * Raised inside {@linkplain RestFn} when it receives a HTTP response that is
 * considered a failure/error.
 * <p>
 * The exception happens only after a {@linkplain HttpResponse} has been
 * received and its status code is not within 200. It doesn't cover anything
 * else.
 * <p>
 * This is a checked exception.
 *
 * @author Lei Yang
 * @since 2.5.0
 */
public sealed class ErrorResponseException
        extends Exception permits ClientErrorException, ServerErrorException, RedirectionException {
    private static final long serialVersionUID = -182048232082907551L;

    protected final RestRequest request;
    protected final HttpResponse<?> response;
    protected final String message;

    public <T> ErrorResponseException(final RestRequest request, final HttpResponse<T> response) {
        if (request == null || response == null || HttpUtils.isSuccess(response.statusCode())) {
            throw new IllegalArgumentException();
        }

        this.request = request;
        this.response = response;
        this.message = this.request.method() + " " + this.request.uri() + ", " + response.statusCode() + " "
                + Optional.ofNullable(response.body()).map(Object::toString).orElse("");
    }

    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> httpResponse() {
        return (HttpResponse<T>) this.response;

    }

    public int statusCode() {
        return this.httpResponse().statusCode();
    }

    /**
     * Returns the response body.
     */
    @SuppressWarnings("unchecked")
    public <T> T body() {
        return (T) this.response.body();
    }

    /**
     * Returns the response headers.
     */
    public HttpHeaders headers() {
        return this.response.headers();
    }

    /**
     * Returns the response headers.
     */
    public List<String> headerValues(final String name) {
        return this.response.headers().allValues(name);
    }

    /**
     * Returns the first value of the header if present on the response. Otherwise,
     * returns the <code>def</code>.
     *
     * @param name header name
     * @param def  the value to return if the response has no value for the header
     * @return header value or <code>def</code>
     */
    public String headerValue(final String name, final String def) {
        return this.response.headers().firstValue(name).orElse(def);
    }

    /**
     * Returns the first value of the response header if present. Otherwise, returns
     * <code>null</code>.
     *
     * @param name header name
     * @return header value or <code>null</code>
     */
    public String headerValue(final String name) {
        return this.response.headers().firstValue(name).orElse(null);
    }

    /**
     * Returns the response headers.
     */
    public Map<String, List<String>> headersMap() {
        return this.response.headers().map();
    }

    public RestRequest restRequest() {
        return request;
    }

    @Override
    public String getMessage() {
        return message;
    }
}