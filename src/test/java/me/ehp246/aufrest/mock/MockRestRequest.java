package me.ehp246.aufrest.mock;

import me.ehp246.aufrest.api.rest.BodyReceiver;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class MockRestRequest implements RestRequest {
    private final String uri = "";
    private BodyReceiver receiver = null;

    public MockRestRequest() {
        super();
    }

    public MockRestRequest withBodyReceiver(final BodyReceiver receiver) {
        this.receiver = receiver;
        return this;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public BodyReceiver bodyReceiver() {
        return this.receiver == null ? RestRequest.super.bodyReceiver() : this.receiver;
    }

}
