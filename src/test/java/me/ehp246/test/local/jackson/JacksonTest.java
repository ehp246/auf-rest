package me.ehp246.test.local.jackson;

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
        "me.ehp246.aufrest.restLogger.enabled:true" })
class JacksonTest {
    @Autowired
    private JacksonCases cases;

    @Test
    void jsonView_01() {
        final var expected = new Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var result = this.cases.post(expected);

        Assertions.assertEquals(expected.username(), result.username());

        Assertions.assertEquals(null, result.password());
    }
}
