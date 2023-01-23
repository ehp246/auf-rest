package me.ehp246.test.embedded.returns;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.Bind;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/header")
interface HeaderReturnCase {
    HttpHeaders get(@OfQuery("value") String value);

    @OfResponse(value = Bind.HEADER, header = "x-aufrest-header")
    String getNamed(@OfQuery("value") String value);

    @OfResponse(value = Bind.HEADER)
    Map<String, List<String>> getMap(@OfQuery("value") String value);

    @OfResponse(value = Bind.HEADER, header = "x-aufrest-header")
    List<String> getList(@OfQuery("value") String value);
}
