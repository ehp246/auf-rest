package me.ehp246.aufrest.integration.local.listener;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

@ByRest("http://localhost:${local.server.port}/filter")
public interface TestCase001 {
    Instant post404(Instant now);

    @OfMapping("/instant")
    Instant post(Instant now);
}
