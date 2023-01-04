package me.ehp246.aufrest.mock;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
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
    public RestFn get(final ClientConfig clientConfig) {
        if (this.except != null) {
            return (req, des, con) -> {
                throw this.except;
            };
        }

        return (req, des, con) -> {
            this.req = req;
            return response;
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
