package me.ehp246.aufrest.integration.local.returntype;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * Receiving different media type as raw String.
 * 
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/json/")
interface TextTestCase001 {
    @OfMapping(value = "instant")
    String getJson();

    @OfMapping(value = "instant", accept = HttpUtils.TEXT_PLAIN)
    String get();

    @OfMapping(value = "instant", contentType = "text/plain", accept = HttpUtils.TEXT_PLAIN)
    String post(String text);

    @OfMapping(value = "instant", accept = HttpUtils.TEXT_PLAIN)
    String post(Instant instant);
}
