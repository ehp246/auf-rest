package me.ehp246.test.embedded.threadcontext;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/echo")
interface ThreadContextCase {
    @OfRequest("/name")
    String get(@OfQuery String name);
}
