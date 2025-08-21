package me.ehp246.test.embedded.stream;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class StreamController {
    @GetMapping("person")
    Person getPerson(@RequestParam(value = "name", required = false) final String name) {
        return new Person(Instant.now(), name);
    }

    @PostMapping("inputstream")
    int postStream(final InputStream in) throws IOException {
        int count = 0;
        // Should be all zeros.
        while (in.read() == 0) {
            count++;
        }
        return count;
    }
}
