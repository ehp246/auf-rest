package me.ehp246.aufrest.integration.local.exception;

import java.io.IOException;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.ServerErrorException;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/auth/basic")
interface TestCase001 {
    void get();

    @OfMapping("/moved")
    void getMoved(@AuthHeader String auth) throws RedirectionException;

    void get(@AuthHeader String auth) throws ClientErrorException, ServerErrorException;

    void post() throws IOException, InterruptedException;
}
