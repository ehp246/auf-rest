package me.ehp246.test.embedded.restfn;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.ehp246.test.embedded.restfn.Logins.Login;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/restfn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class RestFnController {
    @GetMapping("auth")
    void get() {
        /*
         * No return needed.
         */
    }

    @GetMapping("path/{id}")
    String getPath(@PathVariable("id") final String id) {
        return id;
    }

    @GetMapping("login")
    Login getLogin(@RequestBody final Login login) {
        return login;
    }

    @GetMapping("logins")
    Set<Login> getLoginSet(@RequestBody final Login login) {
        return Set.of(login);
    }

    @GetMapping("error")
    ResponseEntity<Error> getError(@RequestHeader("code") final int code,
            @RequestParam("message") final String message) {
        return ResponseEntity.status(410).body(new Error(code, message));
    }

    @GetMapping("status")
    ResponseEntity<Error> getError(@RequestHeader("code") final int code) {
        return ResponseEntity.status(code).body(null);
    }
}
