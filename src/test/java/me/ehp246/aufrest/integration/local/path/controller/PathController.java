package me.ehp246.aufrest.integration.local.path.controller;

import javax.servlet.http.HttpServletRequest;

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
@RequestMapping(value = "/path", produces = MediaType.APPLICATION_JSON_VALUE)
class PathController {
    @GetMapping
    public String get(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/{pathId}")
    public String getWithId(@PathVariable("pathId") final String pathId) {
        return pathId;
    }
}
