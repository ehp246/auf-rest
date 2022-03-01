package me.ehp246.aufrest.integration.local.mime.form;

import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class FormController {

    @PostMapping(value = "string")
    String postForm(@RequestParam("name") final String name) {
        return name;
    }

    @PostMapping(value = "person", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Person postPerson(@RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "lastName") final String lastName, @RequestParam("dob") Instant dob) {
        return new Person(firstName, lastName, dob);
    }

    @PostMapping(value = "person-queryonpath", consumes = MediaType.APPLICATION_JSON_VALUE)
    Person postQueryOnPath(@RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "lastName") final String lastName, @RequestParam("dob") Instant dob) {
        return new Person(firstName, lastName, dob);
    }
}
