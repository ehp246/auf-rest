package me.ehp246.test.mock;

import java.util.UUID;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class MockReq implements RestRequest {
    public final String reqId = UUID.randomUUID().toString();

    @Override
    public String uri() {
        return "http://localhost";
    }

    public static RestRequest withBody(final Object body) {
        return new MockReq() {

            @Override
            public Object body() {
                return body;
            }

        };
    }
}
