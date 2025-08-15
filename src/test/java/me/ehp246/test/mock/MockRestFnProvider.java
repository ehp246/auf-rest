package me.ehp246.test.mock;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.ResponseHandler;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class MockRestFnProvider implements RestFnProvider {
    private final HttpResponse<?> response;
    private RestRequest req;
    private final RuntimeException except;

    public MockRestFnProvider(final HttpResponse<?> response) {
        super();
        this.response = response;
        this.except = null;
    }

    public MockRestFnProvider(final RuntimeException except) {
        super();
        this.response = null;
        this.except = except;
    }

    @Override
    public RestFn get(final RestFnConfig restFnConfig) {
        if (this.except != null) {
            return new RestFn() {

                @Override
                public <T> HttpResponse<T> applyForResponse(final RestRequest request,
                        final ResponseHandler responseDescriptor) {
                    throw except;
                }

            };
        }
        return new RestFn() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> HttpResponse<T> applyForResponse(final RestRequest request,
                    final ResponseHandler responseDescriptor) {
                MockRestFnProvider.this.req = request;
                return (HttpResponse<T>) MockRestFnProvider.this.response;
            }

        };
    }

    public RestRequest takeReq() {
        final var value = this.req;
        this.req = null;
        return value;
    }

    public RestRequest getReq() {
        return this.req;
    }
}
