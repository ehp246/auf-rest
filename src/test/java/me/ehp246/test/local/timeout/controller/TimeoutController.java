package me.ehp246.test.local.timeout.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/timeout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class TimeoutController {

    @GetMapping
    Instant getInstant(@RequestParam(value = "sleep", required = false) final String duration) throws Exception {
        if (duration != null) {
            Thread.sleep(Duration.parse(duration).toMillis());
        }

        return Instant.now();
    }
}
