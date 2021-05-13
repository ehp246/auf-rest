package me.ehp246.aufrest.integration.local.listener;

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
@ActiveProfiles("listenerEx")
class ListenerExTest {
    @Autowired
    private TestCase001 case001;

    @Test
    void test_001() {
        final var ex = Assertions.assertThrows(NullPointerException.class, () -> case001.post(Instant.now()));

        Assertions.assertEquals("onRequest", ex.getMessage());
    }

}
