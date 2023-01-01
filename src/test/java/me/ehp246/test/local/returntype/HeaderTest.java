package me.ehp246.test.local.returntype;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.configuration.AufRestConstants;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        AufRestConstants.REST_LOGGER_ENABLED + "=true" })
class HeaderTest {
    @Autowired
    private HeaderTestCase testCase;

    @Test
    void header_01() {
        final var expected = UUID.randomUUID().toString();

        final var headers = testCase.get(expected);

        Assertions.assertEquals(expected, headers.map().get("x-aufrest-header").get(0));
    }
}
