package me.ehp246.test.embedded.mdc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "mdc", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
class Controller {
    @PostMapping()
    Order post(@RequestBody final Order payload) {
        return payload;
    }

    record Order(String orderId, int amount) {
    }
}
