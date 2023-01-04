package me.ehp246.aufrest.mock;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.BodyDescriptor;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class MockRestFn implements RestFn {
    private HttpResponse<?> response;
    private RestRequest req;
    private ResponseConsumer consumer;
    private final RuntimeException except;

    public MockRestFn() {
        super();
        this.response = null;
        this.except = null;
    }

    public MockRestFn(final RuntimeException except) {
        super();
        this.except = except;
        this.response = null;
    }

    public MockRestFn(final HttpResponse<?> response) {
        super();
        this.except = null;
        this.response = response;
    }

    @Override
    public HttpResponse<?> apply(final RestRequest request, final BodyDescriptor descriptor,
            final ResponseConsumer consumer) {
        this.req = request;
        this.consumer = consumer;

        if (this.except != null) {
            throw this.except;
        }

        this.response = new MockHttpResponse<>();

        return response;
    }

    public ResponseConsumer consumer() {
        return this.consumer;
    }

    public RestRequest req() {
        return this.req;
    }

    public RestRequest takeReq() {
        final var req = this.req;
        this.req = req;

        return req;
    }

    public RestFnProvider toProvider() {
        return cfg -> this;
    }
}
