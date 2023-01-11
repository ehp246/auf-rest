package me.ehp246.test.embedded.exception;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.ServerErrorException;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/status-code/")
interface TestCase {
    @OfMapping("runtime")
    void get();

    @OfMapping("{statusCode}")
    void get(@OfPath("statusCode") int statusCode);

    @OfMapping("{statusCode}")
    void get02(@OfPath("statusCode") int statusCode)
            throws ClientErrorException, ServerErrorException, RedirectionException;

    @OfMapping("{statusCode}")
    void getClientError(@OfPath("statusCode") int statusCode) throws ClientErrorException;

    @OfMapping("{statusCode}")
    void getRedirect(@OfPath("statusCode") int statusCode)
            throws RedirectionException, ErrorResponseException;

    @OfMapping("{statusCode}")
    void getServerError(@OfPath("statusCode") int statusCode) throws ServerErrorException;

    @OfMapping("{statusCode}")
    void getError(@OfPath("statusCode") int statusCode) throws ErrorResponseException;

    @OfMapping("body")
    void getBody(@OfQuery("body") String body) throws ErrorResponseException;
}
