package me.ehp246.test.mock;

import java.net.http.HttpRequest;

import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.BodyOf;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;

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
    public HttpRequest apply(final RestRequest req, final BodyOf<?> descriptor) {
        return this.httpRequest;
    }

}
