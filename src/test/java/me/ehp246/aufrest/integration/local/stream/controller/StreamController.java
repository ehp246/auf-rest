package me.ehp246.aufrest.integration.local.stream.controller;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class StreamController {
    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping("person")
    int postStream(InputStream in) throws JsonParseException, JsonMappingException, IOException {
        int count = 0;
        // Should be all zeros.
        while (in.read() == 0) {
            count++;
        }
        return count;
    }
}
