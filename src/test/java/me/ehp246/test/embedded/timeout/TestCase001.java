package me.ehp246.test.embedded.timeout;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfQuery;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/timeout", timeout = "${api.request.timeout:}")
interface TestCase001 {
    void get();

    void get(@OfQuery("sleep") String sleep);
}
