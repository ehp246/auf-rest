package me.ehp246.test.embedded.listener;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

@ByRest("http://localhost:${local.server.port}/filter")
public interface TestCase {
    Instant post404(Instant now);

    @OfRequest("/instant")
    Instant post(Instant now);

    @OfRequest("/instant")
    Instant get();
}
