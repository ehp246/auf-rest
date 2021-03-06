package me.ehp246.aufrest.integration.local.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class ExTest {
    @Autowired
    private TestCase001 case001;

    @Test
    void test_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, case001::get);

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test_002() {
        final var ex = Assertions.assertThrows(ServerErrorResponseException.class,
                () -> case001.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk"));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_003() {
        final var ex = Assertions.assertThrows(ClientErrorResponseException.class,
                () -> case001.get("Basic YmFzaWN1c2VyOnBhc3N3b3"));

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test_004() {
        final var ex = Assertions.assertThrows(RedirectionResponseException.class,
                () -> case001.getMoved("Basic YmFzaWN1c2VyOnBhc3N3b3Jk"));

        Assertions.assertEquals(301, ex.statusCode());
    }

    @Test
    void test_006() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> case001.get600("Basic YmFzaWN1c2VyOnBhc3N3b3Jk"));

        Assertions.assertEquals(600, ex.statusCode());
    }
}
