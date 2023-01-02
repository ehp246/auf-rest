package me.ehp246.test.local.logger;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
interface LoggerCases {
    @ByRest("http://localhost:${local.server.port}/logger")
    interface LoggerCase01 {
        Instant post(final Instant instant);

        Instant post(final Instant instant, @AuthHeader String auth);

        @OfMapping("/null")
        Instant postNull(final Instant instant, @AuthHeader String auth);
    }

    @ByRest("http://localhost:0/logger")
    interface LoggerCase02 {
        Instant post(final Instant instant);
    }
}