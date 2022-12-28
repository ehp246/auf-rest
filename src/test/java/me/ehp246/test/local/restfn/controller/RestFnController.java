package me.ehp246.test.local.restfn.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/restfn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class RestFnController {
    @GetMapping("auth")
    void get() {
    }

    @GetMapping("path/{id}")
    String getPath(@PathVariable("id") String id) {
        return id;
    }
}
