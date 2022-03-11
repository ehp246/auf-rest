package me.ehp246.aufrest.mock;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class MockRestRequest implements RestRequest {
    private final String uri = "";

    public MockRestRequest() {
        super();
    }

    @Override
    public String uri() {
        return uri;
    }
}
