package me.ehp246.aufrest.integration.local.exception.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/status-code/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class ExController {
    @GetMapping("runtime")
    void get() {
        throw new RuntimeException();
    }

    @GetMapping("{statusCode}")
    ResponseEntity<String> get(@PathVariable("statusCode") int statusCode) {
        return ResponseEntity.status(statusCode).body(statusCode + "");
    }
}
