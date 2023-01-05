package me.ehp246.test.embedded.query;

import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfQuery;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/query")
interface QueryCase {
    @OfMapping("/name")
    Map<String, String> getSingle(@OfQuery("firstName") String firstName, @OfQuery("lastName") String lastName);

    @OfMapping("/names")
    Map<String, List<String>> getList(@OfQuery List<String> firstName, @OfQuery List<String> lastName);
}
