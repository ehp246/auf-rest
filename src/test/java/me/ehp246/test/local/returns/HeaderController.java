package me.ehp246.test.local.returns;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
@RequestMapping(value = "/header", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class HeaderController {
    @GetMapping
    Map<String, String> get(@RequestParam() final String value, final HttpServletResponse response) {
        response.setHeader("x-aufrest-header", value);
        return Map.of("x-aufrest-header", value);
    }
}
