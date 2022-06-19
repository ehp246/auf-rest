package me.ehp246.aufrest.core.byrest.perf;

import java.time.Instant;

import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest("${uri}")
interface PerfTestCase01 {
    @OfMapping("/clock/{clockName}")
    Instant get(@PathParam("clockName") String clockName, @RequestParam("question1") String query,
            @AuthHeader String auth, String payload);
}
