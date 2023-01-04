package me.ehp246.test.embedded.exception;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/status-code/")
interface TestCase {
    @OfMapping("runtime")
    void get();

    @OfMapping("{statusCode}")
    void get(@OfPath("statusCode") int statusCode);

    @OfMapping("{statusCode}")
    void get02(@OfPath("statusCode") int statusCode)
            throws ClientErrorResponseException, ServerErrorResponseException, RedirectionResponseException;

    @OfMapping("{statusCode}")
    void getClientError(@OfPath("statusCode") int statusCode) throws ClientErrorResponseException;

    @OfMapping("{statusCode}")
    void getRedirect(@OfPath("statusCode") int statusCode)
            throws RedirectionResponseException, ErrorResponseException;

    @OfMapping("{statusCode}")
    void getError(@OfPath("statusCode") int statusCode) throws ErrorResponseException;

    @OfMapping("body")
    void getBody(@OfQuery("body") String body) throws ErrorResponseException;
}
