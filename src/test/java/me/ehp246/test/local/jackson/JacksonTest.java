package me.ehp246.test.local.jackson;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
"me.ehp246.aufrest.restLogger.enabled:true" })
class JacksonTest {
    @Autowired
    private JsonViewCases cases;

    @Test
    void jsonView_01() {
        final var expected = new Logins.Login1(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var result = this.cases.post1(expected);

        Assertions.assertEquals(expected.username(), result.username());

        Assertions.assertEquals(null, result.password());
    }

    @Test
    void jsonView_02() throws JsonProcessingException {
        final var expected = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var result = this.cases.post2(expected);

        Assertions.assertEquals(null, result.getUsername());

        Assertions.assertEquals(expected.password(), result.getPassword());
    }

    @Test
    void jsonView_03() {
        final var expected = new Logins.Login1(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var result = this.cases.post3(expected);

        Assertions.assertEquals(null, result.username());

        Assertions.assertEquals(expected.password(), result.password());
    }
}
