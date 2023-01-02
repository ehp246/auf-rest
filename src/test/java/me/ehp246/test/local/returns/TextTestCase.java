package me.ehp246.test.local.returns;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * Receiving different media type as raw String.
 *
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/json/")
interface TextTestCase {
    @OfMapping("instant")
    String getJson();

    @OfMapping("person")
    @AsIs
    String getPerson(@OfQuery("name") String name);

    @OfMapping(value = "instant", accept = HttpUtils.TEXT_PLAIN)
    String get();

    @OfMapping(value = "instant", contentType = "text/plain", accept = HttpUtils.TEXT_PLAIN)
    String post(String text);

    @OfMapping(value = "instant", accept = HttpUtils.TEXT_PLAIN)
    String post(Instant instant);
}
