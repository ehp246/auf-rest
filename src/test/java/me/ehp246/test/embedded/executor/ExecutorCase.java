package me.ehp246.test.embedded.executor;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/query")
interface ExecutorCase {
    @OfRequest("/name")
    String get(HttpResponse.BodyHandler<String> hanlder);
}
