package me.ehp246.test.local.returns;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfQuery;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header/")
interface OfHeaderTestCase {
    HttpHeaders get(@OfQuery("value") String value);

    @OfHeader("x-aufrest-header")
    String getNamed(@OfQuery("value") String value);

    @OfHeader
    Map<String, List<String>> getMap(@OfQuery("value") String value);

    @OfHeader("x-aufrest-header")
    List<String> getList(@OfQuery("value") String value);
}
