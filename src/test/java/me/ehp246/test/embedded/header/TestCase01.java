package me.ehp246.test.embedded.header;

import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header")
interface TestCase01 {
    Headers get();

    Headers get(@OfHeader("x-req-id") String value);

    Headers get(@OfHeader("x-req-id") int reqId, @OfHeader("x-req-id") String reqId2);

    Headers get(@OfHeader int reqId);

    Headers get(@OfHeader Map<String, List<String>> headers);

    Headers getWithMap2(@OfHeader Map<String, String> headers);
}
