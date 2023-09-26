package me.ehp246.test.embedded.threadcontext;

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
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class ThreadContextTest {
    @Autowired
    private ThreadContextCase testCase;

    @Test
    void test_01() {
        final var expected = UUID.randomUUID().toString();

        Assertions.assertEquals(expected, testCase.get(expected));
    }

}
