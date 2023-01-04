package me.ehp246.test.embedded.errortype;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.exception.ErrorResponseException;

/**
 * @author Lei Yang
 *
 */
interface ErrorTypeCase {

    @ByRest(value = "http://localhost:${local.server.port}/status-code/")
    interface Case01 {
        @OfMapping("body")
        void getBody(@OfQuery("body") String body) throws ErrorResponseException;
    }

    @ByRest(value = "http://localhost:${local.server.port}/status-code/", errorType = Map.class)
    interface Case02 {
        @OfMapping("body")
        void getBody(@OfQuery("body") String body) throws ErrorResponseException;
    }

    @ByRest(value = "http://localhost:${local.server.port}/status-code/", errorType = String.class)
    interface Case03 {
        @OfMapping("body")
        void getBody(@OfQuery("body") String body) throws ErrorResponseException;
    }
}
