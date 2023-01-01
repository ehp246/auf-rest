package me.ehp246.test.local.returns;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header/")
interface OfHeaderTestCase {
    HttpHeaders get(@RequestParam("value") String value);

    @OfHeader("x-aufrest-header")
    String getNamed(@RequestParam("value") String value);

    @OfHeader
    Map<String, List<String>> getMap(@RequestParam("value") String value);

    @OfHeader("x-aufrest-header")
    List<String> getList(@RequestParam("value") String value);
}
