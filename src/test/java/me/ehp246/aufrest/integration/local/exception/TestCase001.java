package me.ehp246.aufrest.integration.local.exception;

import java.io.IOException;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.exception.BadRequestException;
import me.ehp246.aufrest.api.exception.ServerFailureException;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/auth/basic")
interface TestCase001 {
    void get() throws BadRequestException;

    void get(@AuthHeader String auth) throws ServerFailureException;

    void post() throws IOException, InterruptedException;
}
