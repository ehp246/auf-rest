package me.ehp246.aufrest.integration.local.mime.form;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/form", contentType = "application/x-www-form-urlencoded")
interface FormCase {
    String post(Map<String, String> map);

    @OfMapping(value = "/string")
    @AsIs
    String post(@RequestParam("name") String name);

    @OfMapping(value = "/person")
    Person postQueryInBody(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("dob") Instant dob);

    @OfMapping(value = "/person-queryonpath", contentType = "application/json")
    Person postQueryOnPath(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("dob") Instant dob);

    @OfMapping(value = "/querymap")
    List<String> postQueryMapOnPath(@RequestParam Map<String, String> map,
            @RequestParam("qList") List<String> list, @RequestParam("qList") String query);
}
