package me.ehp246.test.embedded.body;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body", acceptGZip = false)
interface BodyHandlerCase {
    @OfRequest(value = "/publisher")
    int postNumber(String number, BodyHandler<Integer> bodyHandler);

    @OfRequest(value = "/publisher")
    @OfResponse(handler = "onMethod")
    String postOnMethod(String payload);

    @OfRequest(value = "/publisher")
    @OfResponse(handler = "onInterface")
    String postOnInterface(String payload);
}
