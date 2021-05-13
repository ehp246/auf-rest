package me.ehp246.aufrest.integration.local.xml.controller;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
@RequestMapping(value = "/xml", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
class XmlController {

    @GetMapping("instants")
    List<Instant> getInstants(@RequestParam("count") final int count) {
        return IntStream.range(0, count).mapToObj(i -> Instant.now()).collect(Collectors.toList());
    }

    @GetMapping("instant")
    Instant getInstant() {
        return Instant.now();
    }

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

    @GetMapping("persons")
    List<Person> getPersons(@RequestParam(value = "count", defaultValue = "1") final int count) {
        return IntStream.range(0, count).mapToObj(i -> (Person) Instant::now).collect(Collectors.toList());
    }
}
