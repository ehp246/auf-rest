package me.ehp246.test.embedded.exception;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.exception.BadRequestException;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.ForbiddenException;
import me.ehp246.aufrest.api.exception.NotAcceptableException;
import me.ehp246.aufrest.api.exception.NotAllowedException;
import me.ehp246.aufrest.api.exception.NotAuthorizedException;
import me.ehp246.aufrest.api.exception.NotFoundException;
import me.ehp246.aufrest.api.exception.NotSupportedException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.ServerErrorException;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/status-code/")
interface TestCase {
    @OfRequest("runtime")
    void get();

    @OfRequest("{statusCode}")
    void get(@OfPath("statusCode") int statusCode);

    @OfRequest("{statusCode}")
    void get02(@OfPath("statusCode") int statusCode)
            throws ClientErrorException, ServerErrorException, RedirectionException;

    @OfRequest("{statusCode}")
    void getClientError(@OfPath("statusCode") int statusCode)
            throws ClientErrorException, BadRequestException, ForbiddenException, NotAcceptableException,
            NotAllowedException, NotAuthorizedException, NotFoundException, NotSupportedException;

    @OfRequest("{statusCode}")
    void getRedirect(@OfPath("statusCode") int statusCode)
            throws RedirectionException, ErrorResponseException;

    @OfRequest("{statusCode}")
    void getServerError(@OfPath("statusCode") int statusCode) throws ServerErrorException;

    @OfRequest("{statusCode}")
    void getError(@OfPath("statusCode") int statusCode) throws ErrorResponseException;

    @OfRequest("body")
    void getBody(@OfQuery("body") String body) throws ErrorResponseException;
}
