package me.ehp246.test.mock;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.BodyOf;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.BodyHandlerType;

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
    public RestFn get(final ClientConfig clientConfig) {
        if (this.except != null) {
            return new RestFn() {

                @Override
                public <T> HttpResponse<T> applyForResponse(final RestRequest request,
                        final BodyOf<?> requestDescriptor,
                        final BodyHandlerType<T> responseDescriptor) {
                    throw except;
                }

            };
        }
        return new RestFn() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> HttpResponse<T> applyForResponse(final RestRequest request,
                    final BodyOf<?> requestDescriptor, final BodyHandlerType<T> responseDescriptor) {
                MockRestFnProvider.this.req = request;
                return (HttpResponse<T>) MockRestFnProvider.this.response;
            }

        };
    }

    public RestRequest takeReq() {
        final var req = this.req;
        this.req = null;
        return req;
    }

    public RestRequest getReq() {
        return this.req;
    }
}
