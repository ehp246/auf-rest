package me.ehp246.test.local.path;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfPath;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/path")
interface TestCase001 {
    @AsIs
    String get();

    @AsIs
    @OfMapping("/{pathId}")
    String get(@OfPath("pathId") String pathId);
}
