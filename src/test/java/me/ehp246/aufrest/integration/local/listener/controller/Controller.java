package me.ehp246.aufrest.integration.local.listener.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class Controller {
    @PostMapping("instant")
    Instant postInstant(@RequestBody final Instant instant) {
        return instant.minus(10, ChronoUnit.MINUTES);
    }
}
