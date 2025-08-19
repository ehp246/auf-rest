package me.ehp246.test.embedded.listener;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("listenerException")
class ListenerExceptionTest {
    @Autowired
    private TestCase case1;

    @Test
    void test_001() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(NullPointerException.class, () -> case1.post(now));

        Assertions.assertEquals("onRequest from listenerException", ex.getMessage());
    }
}
