package me.ehp246.test.embedded.body;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "body", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class BodyController {
    @PostMapping(value = "publisher")
    List<String> postString(@RequestBody String payload) {
        return List.of(payload);
    }

    @PostMapping(value = "publisher/query", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    List<String> postQueryParams(@RequestParam("first") String firstName, @RequestParam("last") String lastName) {
        return List.of(firstName, lastName);
    }

    @PostMapping(value = "person")
    String postPerson(@RequestBody String payload) {
        return payload;
    }

    @PostMapping(value = "personName")
    String postPersonName(@RequestBody String payload) {
        return payload;
    }

    @PostMapping(value = "personDob")
    String postPersonDob(@RequestBody String payload) {
        return payload;
    }
}
