package me.ehp246.aufrest.mock;

import java.net.http.HttpRequest;

import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.BodyDescriptor;
import me.ehp246.aufrest.api.rest.HttpRequestBuilder;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class MockHttpRequestBuilder implements HttpRequestBuilder {
    private HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

    public MockHttpRequestBuilder() {
        super();
    }

    public MockHttpRequestBuilder(final HttpRequest httpRequest) {
        super();
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpRequest apply(final RestRequest req, final BodyDescriptor descriptor) {
        return this.httpRequest;
    }

}
