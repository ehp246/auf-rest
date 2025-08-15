package me.ehp246.test.embedded.view;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled:true" })
class ViewTest {
    @Autowired
    private ViewCases cases;

    @Test
    void requestView_01() {
        final var expected = new Logins.RequestWithView(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var result = this.cases.postRequestAllBlank(expected);

        Assertions.assertEquals(null, result.username());
        Assertions.assertEquals(null, result.password());
    }

    @Test
    void requestView_02() {
        final var expected = new Logins.RequestWithView(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var result = this.cases.postRequestWithPassword(expected);

        Assertions.assertEquals(null, result.username());
        Assertions.assertEquals(expected.password(), result.password());
    }

    @Test
    void responseView_01() {
        final var expected = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var result = this.cases.postResponseWithDefault(expected);

        Assertions.assertEquals(expected.username(), result.getUsername());
        Assertions.assertEquals(expected.password(), result.getPassword());
    }

    @Test
    void responseView_02() {
        final var expected = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var result = this.cases.postResponseWithPassword(expected);

        Assertions.assertEquals(null, result.getUsername());
        Assertions.assertEquals(expected.password(), result.getPassword());
    }
}
