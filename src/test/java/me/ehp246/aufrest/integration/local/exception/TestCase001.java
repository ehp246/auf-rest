package me.ehp246.aufrest.integration.local.exception;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/auth/basic")
interface TestCase001 {
    void get();

    @OfMapping("/moved")
    void getMoved(@AuthHeader String auth) throws RedirectionResponseException;

    void get(@AuthHeader String auth) throws ClientErrorResponseException, ServerErrorResponseException;

    @OfMapping("/600")
    void get600(@AuthHeader String auth);
}
