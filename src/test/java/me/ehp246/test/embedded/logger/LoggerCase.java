package me.ehp246.test.embedded.logger;

import java.time.Instant;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.exception.NotFoundException;

/**
 * @author Lei Yang
 *
 */
interface LoggerCases {
    @ByRest("http://localhost:${local.server.port}/logger")
    interface LoggerCase01 {
        Instant post(final Instant instant);

        Instant post(final Instant instant, @OfAuth String auth);

        @OfRequest("/null")
        Instant postNull(final Instant instant, @OfAuth String auth);

        @OfRequest("/null")
        Instant postNullThrowing(final Instant instant, @OfAuth String auth) throws NotFoundException;
    }

    @ByRest("http://localhost:0/logger")
    interface LoggerCase02 {
        Instant post(final Instant instant);
    }
}