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
    private final HttpResponse<Object> response;
    private RestRequest req;

    public MockRestFnProvider(final HttpResponse<Object> response) {
        super();
        this.response = response;
    }

    @Override
    public RestFn get(final ClientConfig clientConfig) {
        return (req, pub, con) -> {
            this.req = req;
            return response;
        };
    }

    public RestRequest takeReq() {
        final var req = this.req;
        this.req = null;
        return req;
    }
}
