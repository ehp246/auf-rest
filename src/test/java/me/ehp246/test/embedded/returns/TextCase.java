package me.ehp246.test.embedded.returns;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * Receiving different media type as raw String.
 *
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/json/")
interface TextCase {
    @OfRequest("instant")
    String getJson();

    @OfRequest("person")
    String getPerson(@OfQuery("name") String name);

    @OfRequest(value = "instant", accept = HttpUtils.TEXT_PLAIN)
    String get();

    @OfRequest(value = "instant", contentType = "text/plain", accept = HttpUtils.TEXT_PLAIN)
    String post(String text);

    @OfRequest(value = "instant", accept = HttpUtils.TEXT_PLAIN)
    String post(Instant instant);
}
