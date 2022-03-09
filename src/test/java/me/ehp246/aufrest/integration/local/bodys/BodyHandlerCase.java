package me.ehp246.aufrest.integration.local.bodys;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body", acceptGZip = false)
interface BodyHandlerCase {
    @OfMapping(value = "/publisher")
    int postNumber(BodyHandler<Integer> bodyHandler, String number);

}
