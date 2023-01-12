package me.ehp246.test.embedded.path;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/path")
interface TestCase001 {
    String get();

    @OfRequest("/{pathId}")
    String get(@OfPath("pathId") String pathId);
}
