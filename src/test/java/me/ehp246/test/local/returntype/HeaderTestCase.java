package me.ehp246.test.local.returntype;

import java.net.http.HttpHeaders;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header/")
interface HeaderTestCase {
    HttpHeaders get(@RequestParam("value") String value);

    String getNamed();

    Map<String, String> getMap();
}
