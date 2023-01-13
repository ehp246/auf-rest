package me.ehp246.test.embedded.resttemplate;

import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "resttemplate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class RestTemplateController {
    @GetMapping(value = "person")
    Person get(@RequestParam() final String firstName, @RequestParam() final String lastName,
            @RequestParam() final Instant dob) {
        return new Person(firstName, lastName, dob);
    }

    @PostMapping(value = "person/{firstName}/{lastName}")
    Person post(@PathVariable() final String firstName, @PathVariable() final String lastName,
            @RequestBody() final Person person) {
        return person;
    }
}
