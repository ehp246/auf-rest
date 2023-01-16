package me.ehp246.test.embedded.path;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByRest("http://localhost:${local.server.port}/path")
    interface Case01 {
        String get();

        @OfRequest("/{pathId}")
        String get(@OfPath("pathId") String pathId);
    }

    @ByRest("http://localhost:${local.server.port}/{root}")
    interface Case02 {
        @OfRequest("/{pathId}")
        String get(@OfPath String pathId, @OfPath String root);

        @OfRequest("/{pathId}")
        String get(@OfPath("pathId") String pathId);
    }
}