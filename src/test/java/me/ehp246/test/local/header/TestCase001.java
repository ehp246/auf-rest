package me.ehp246.test.local.header;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.integration.model.Headers;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header")
interface TestCase001 {
    Headers get();

    Headers get(@RequestHeader("x-req-id") String value);

    Headers get(@RequestHeader Map<String, List<String>> headers);

    Headers getWithMap2(@RequestHeader Map<String, String> headers);
}
