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

    @OfHeader("value")
    String getNamed(@RequestParam("value") String value);

    @OfHeader
    Map<String, String> getMap();

    @OfHeader({ "value", "content-length" })
    Map<String, String> getAllMap();

    @OfHeader({ "value" })
    List<String> getList(@RequestParam("value") String value);
}
