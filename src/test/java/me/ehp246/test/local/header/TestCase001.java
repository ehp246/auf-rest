package me.ehp246.test.local.header;

import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.integration.model.Headers;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header")
interface TestCase001 {
    Headers get();

    Headers get(@OfHeader("x-req-id") String value);

    Headers get(@OfHeader Map<String, List<String>> headers);

    Headers getWithMap2(@OfHeader Map<String, String> headers);
}
