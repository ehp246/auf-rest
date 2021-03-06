package me.ehp246.aufrest.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = ByRestConfiguration.class)
@Import(Jackson.class)
class ByRestConfigurationTest {

    @Test
    void test_001() {
        final var clientConfig = new ByRestConfiguration().restClientConfig("", req -> null);

        Assertions.assertEquals(null, clientConfig.connectTimeout());
    }

    @Test
    void test_002() {
        final var clientConfig = new ByRestConfiguration().restClientConfig(null, null);

        Assertions.assertEquals(null, clientConfig.connectTimeout());
    }

    @Test
    void test_004() {
        Assertions.assertDoesNotThrow(() -> new ByRestConfiguration().restClientConfig(null, null));
    }

    @Test
    void test_005() {
        final var clientConfig = new ByRestConfiguration().restClientConfig("PT1S", null);

        Assertions.assertEquals(1000, clientConfig.connectTimeout().toMillis());
    }
}
