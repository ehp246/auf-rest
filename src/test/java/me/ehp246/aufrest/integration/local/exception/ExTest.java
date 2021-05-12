package me.ehp246.aufrest.integration.local.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.BadRequestException;
import me.ehp246.aufrest.api.exception.ServerFailureException;

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
        final var ex = Assertions.assertThrows(BadRequestException.class, case001::get);

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test_002() {
        final var ex = Assertions.assertThrows(ServerFailureException.class,
                () -> case001.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk"));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_003() {
        final var ex = Assertions.assertThrows(BadRequestException.class,
                () -> case001.get("Basic YmFzaWN1c2VyOnBhc3N3b3"));

        Assertions.assertEquals(401, ex.statusCode());
    }
}
