package me.ehp246.test.local.path;

import org.springframework.web.bind.annotation.PathVariable;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

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
    String get(@PathVariable("pathId") String pathId);
}
