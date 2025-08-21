package me.ehp246.test.embedded.auth.login;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.random.RandomGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
class LoginController {
    private final AtomicReference<Account> loggedInRef = new AtomicReference<>();

    @PostMapping("token")
    Token getToken(@RequestBody final Account account) {
        this.loggedInRef.set(account);
        return new Token(createToken(account), Instant.now().plusSeconds(60));
    }

    @GetMapping("/account/balance")
    int getBalance(@RequestHeader("Authorization") final String token) {
        final var loggedIn = this.loggedInRef.get();

        if (!this.createToken(loggedIn).equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return Math.abs(RandomGenerator.getDefault().nextInt()) + 100;
    }

    private String createToken(final Account account) {
        return account.id() + ":" + account.id();
    }

}
