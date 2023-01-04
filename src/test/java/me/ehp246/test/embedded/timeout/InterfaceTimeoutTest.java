package me.ehp246.test.embedded.timeout;

import java.net.http.HttpTimeoutException;

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
@TestPropertySource(properties = { "me.ehp246.aufrest.responseTimeout=PT0.01S", "api.request.timeout=PT0.5S" })
class InterfaceTimeoutTest {
    @Autowired
    private TestCase001 case001;

    @Test
    void test_001() {
        case001.get();
    }

    @Test
    void test_002() {
        Assertions.assertEquals(true, Assertions.assertThrows(Exception.class, () -> case001.get("PT0.6S"))
                .getCause() instanceof HttpTimeoutException);
    }
}
