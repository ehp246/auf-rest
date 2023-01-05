package me.ehp246.test.embedded.query;

import java.util.List;
import java.util.Map;

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
@RequestMapping(value = "query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class QueryController {
    @GetMapping(value = "name")
    Map<String, String> postSingle(@RequestParam("firstName") final String firstName,
            @RequestParam("lastName") final String lastName) {
        return Map.of("firstName", firstName, "lastName", lastName);
    }

    @GetMapping(value = "names")
    Map<String, List<String>> postList(@RequestParam("firstName") final List<String> firstName,
            @RequestParam("lastName") final List<String> lastName) {
        return Map.of("firstName", firstName, "lastName", lastName);
    }
}
