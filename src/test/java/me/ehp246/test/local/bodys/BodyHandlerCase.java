package me.ehp246.test.local.bodys;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body", acceptGZip = false, responseBodyHandler = "onInterface")
interface BodyHandlerCase {
    @OfMapping(value = "/publisher")
    int postNumber(String number, BodyHandler<Integer> bodyHandler);

    @OfMapping(value = "/publisher", responseBodyHandler = "onMethod")
    String postOnMethod(String payload);

    @OfMapping(value = "/publisher", responseBodyHandler = "onInterface")
    String postOnInterface(String payload);
}
