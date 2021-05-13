package me.ehp246.aufrest.integration.local.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/auth/basic", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class ExController {
    @GetMapping
    void get() {
        throw new RuntimeException();
    }

    @GetMapping("/moved")
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    void getMoved() {
    }
}
