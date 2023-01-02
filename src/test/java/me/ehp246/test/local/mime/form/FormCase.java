package me.ehp246.test.local.mime.form;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfQuery;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/form", contentType = "application/x-www-form-urlencoded")
interface FormCase {
    String post(Map<String, String> map);

    @OfMapping(value = "/string")
    @AsIs
    String post(@OfQuery("name") String name);

    @OfMapping(value = "/person")
    Person postQueryInBody(@OfQuery("firstName") String firstName, @OfQuery("lastName") String lastName,
            @OfQuery("dob") Instant dob);

    @OfMapping(value = "/person-queryonpath", contentType = "application/json")
    Person postQueryOnPath(@OfQuery("firstName") String firstName, @OfQuery("lastName") String lastName,
            @OfQuery("dob") Instant dob);

    @OfMapping(value = "/querymap")
    List<String> postQueryMap(@OfQuery Map<String, String> map, @OfQuery("qList") List<String> list,
            @OfQuery("qList") String query);
}
