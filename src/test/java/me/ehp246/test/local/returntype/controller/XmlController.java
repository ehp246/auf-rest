package me.ehp246.test.local.returntype.controller;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/xml", consumes = "application/*", produces = MediaType.APPLICATION_XML_VALUE)
class XmlController {

    @GetMapping(value = "instants")
    List<Instant> getInstants(@RequestParam("count") final int count) {
        return IntStream.range(0, count).mapToObj(i -> Instant.now()).collect(Collectors.toList());
    }

    @GetMapping("instant")
    Instant getInstant() {
        return Instant.now();
    }

    @GetMapping("person")
    Person getPerson(@RequestParam("name") final String name) {
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

    @GetMapping("persons")
    List<Person> getPersons(@RequestParam(value = "count", defaultValue = "1") final int count) {
        return IntStream.range(0, count).mapToObj(i -> new Person() {

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Instant getDob() {
                return Instant.now();
            }
        }).collect(Collectors.toList());
    }

    @GetMapping("text/{text}")
    String getString(@PathVariable("text") String text) {
        return text;
    }
}
