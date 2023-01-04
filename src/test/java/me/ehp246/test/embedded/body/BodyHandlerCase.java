package me.ehp246.test.embedded.body;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body", acceptGZip = false, consumerHandler = "onInterface")
interface BodyHandlerCase {
    @OfMapping(value = "/publisher")
    int postNumber(String number, BodyHandler<Integer> bodyHandler);

    @OfMapping(value = "/publisher", consumerHandler = "onMethod")
    String postOnMethod(String payload);

    @OfMapping(value = "/publisher", consumerHandler = "onInterface")
    String postOnInterface(String payload);
}