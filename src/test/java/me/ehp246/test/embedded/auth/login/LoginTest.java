package me.ehp246.test.embedded.auth.login;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.test.embedded.auth.login.TestCases.Case01;
import me.ehp246.test.embedded.auth.login.TestCases.Case02;
import me.ehp246.test.embedded.auth.login.TestCases.Login;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class LoginTest {
    @Autowired
    private Login login;

    @Autowired
    private Case01 case01;

    @Autowired
    private Case02 case02;

    @Test
    void login_01() {
        final var account = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var token = login.getToken(account);

        final var balance = case01.getBalance(token.token());

        Assertions.assertEquals(true, balance > 100);

        login.getToken(new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

        final var token2 = token.token();
        final var httpRespon = Assertions
                .assertThrows(UnhandledResponseException.class, () -> case01.getBalance(token2)).getCause()
                .httpResponse();

        Assertions.assertEquals(401, httpRespon.statusCode());
    }

    @Test
    void login_02() {
        final var account = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var balance = this.case02.getBalance(account);

        Assertions.assertEquals(true, balance > 100);
    }
}
