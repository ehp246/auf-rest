package me.ehp246.aufrest.integration.local.returntype.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
class FormController {
    @GetMapping("person")
    ResponseEntity<Person> get() {
        return new ResponseEntity<Person>(new Person() {

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Instant getDob() {
                return Instant.now();
            }
        }, HttpStatus.OK);
    }

    @PostMapping("person")
    void receive(@RequestBody final MultiValueMap<String, String> name) {

    }
}
