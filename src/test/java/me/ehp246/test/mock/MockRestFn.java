package me.ehp246.test.mock;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor.Provided;

/**
 * @author Lei Yang
 *
 */
public class MockRestFn implements RestFn {
    private RestRequest req;
    private HttpResponse<?> response;

    private RestResponseDescriptor<?> responseDescriptor;
    private final RuntimeException except;

    public MockRestFn() {
        super();
        this.response = new MockHttpResponse<>();
        this.except = null;
    }

    public MockRestFn(final RuntimeException except) {
        super();
        this.except = except;
        this.response = new MockHttpResponse<>();
    }

    public MockRestFn(final HttpResponse<?> response) {
        super();
        this.except = null;
        this.response = response;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> HttpResponse<T> applyForResponse(final RestRequest request,
            final RestBodyDescriptor<?> requestDescriptor,
            final RestResponseDescriptor<T> responseDescriptor) {
        this.req = request;
        this.responseDescriptor = responseDescriptor;

        if (this.except != null) {
            throw this.except;
        }

        return (HttpResponse<T>) response;
    }

    public RestResponseDescriptor<?> responseDescriptor() {
        return this.responseDescriptor;
    }

    public BodyHandler<?> handler() {
        return ((Provided<?>) responseDescriptor).handler();
    }

    public RestRequest req() {
        return this.req;
    }

    public void reset() {
        this.req = null;
        this.response = null;
    }

    public RestFnProvider toProvider() {
        return cfg -> this;
    }
}
