package me.ehp246.test.embedded.jackson;

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
@RequestMapping(value = "body", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class BodyController {
    @PostMapping(value = "login")
    Logins.Login login(@RequestBody final Logins.Login payload) {
        return payload;
    }
}
