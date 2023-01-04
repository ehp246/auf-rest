package me.ehp246.test.embedded.cache;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "cache", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class CacheController {
    private int count = 0;

    @PostMapping(value = "inc")
    int postCount() {
        return ++count;
    }
}
