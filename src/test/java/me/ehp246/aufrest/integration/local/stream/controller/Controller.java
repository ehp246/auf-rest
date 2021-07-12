package me.ehp246.aufrest.integration.local.stream.controller;

import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class Controller {
    @GetMapping("person")
    Person getPerson(@RequestParam(value = "name", required = false) final String name) {
        return new Person() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Instant getDob() {
                return Instant.now();
            }
        };
    }
}
