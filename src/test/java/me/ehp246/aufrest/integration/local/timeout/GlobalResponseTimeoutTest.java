package me.ehp246.aufrest.integration.local.timeout;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "me.ehp246.aufrest.responseTimeout=PT0.01S" })
class GlobalResponseTimeoutTest {
    @Autowired
    private TestCase001 case001;

    @Test
    void test_001() {
        Assertions.assertThrows(Exception.class, case001::get);
    }

}
