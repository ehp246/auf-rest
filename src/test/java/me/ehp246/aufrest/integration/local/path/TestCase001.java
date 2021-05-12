package me.ehp246.aufrest.integration.local.path;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/path")
interface TestCase001 {
    @AsIs
    String get();
}
