package me.ehp246.aufrest.mock;

import java.util.UUID;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.InvokedOn;

/**
 * @author Lei Yang
 *
 */
public class MockReq implements RestRequest {
    public final String reqId = UUID.randomUUID().toString();

    @Override
    public String uri() {
        return "http://nowhere.com";
    }

    @Override
    public InvokedOn invokedOn() {
        return null;
    }

}
