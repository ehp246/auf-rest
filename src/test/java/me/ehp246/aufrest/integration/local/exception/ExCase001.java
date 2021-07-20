package me.ehp246.aufrest.integration.local.exception;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/status-code/")
interface ExCase001 {
    @OfMapping("runtime")
    void get();

    @OfMapping("{statusCode}")
    void get(@PathVariable("statusCode") int statusCode);

    @OfMapping("{statusCode}")
    void get02(@PathVariable("statusCode") int statusCode)
            throws ClientErrorResponseException, ServerErrorResponseException, RedirectionResponseException;

    @OfMapping("{statusCode}")
    void getClientError(@PathVariable("statusCode") int statusCode) throws ClientErrorResponseException;

    @OfMapping("{statusCode}")
    void getRedirect(@PathVariable("statusCode") int statusCode)
            throws RedirectionResponseException, ErrorResponseException;

    @OfMapping("{statusCode}")
    void getError(@PathVariable("statusCode") int statusCode) throws ErrorResponseException;
    
    @OfMapping("body")
    void getBody(@RequestParam("body") String body) throws ErrorResponseException;
}
