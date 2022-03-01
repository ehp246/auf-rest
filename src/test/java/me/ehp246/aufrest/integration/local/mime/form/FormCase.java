package me.ehp246.aufrest.integration.local.mime.form;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/", contentType = "application/x-www-form-urlencoded")
interface FormCase {
    String post(Map<String, String> map);

    @OfMapping(value = "urlencoded")
    String post(@RequestParam("name") String name);

    String post(@RequestParam("name") String name, @RequestParam("dob") Instant dob);
}
