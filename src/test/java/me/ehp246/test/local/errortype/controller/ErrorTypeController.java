package me.ehp246.test.local.errortype.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/status-code/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class ErrorTypeController {
    @GetMapping("{statusCode}")
    ResponseEntity<String> get(@PathVariable("statusCode") int statusCode,
            @RequestParam(value = "body", required = false) final String body) {
        return ResponseEntity.status(statusCode).body(body == null ? statusCode + "" : body);
    }

    @GetMapping("body")
    ResponseEntity<String> get(@RequestParam(value = "body", required = false) final String body) {
        return ResponseEntity.status(400).body(body);
    }
}
