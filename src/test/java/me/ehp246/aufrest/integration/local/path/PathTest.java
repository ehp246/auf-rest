package me.ehp246.aufrest.integration.local.path;

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
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class PathTest {
    @Autowired
    private TestCase001 case001;

    @Test
    void path_01() {
        Assertions.assertEquals("/path", case001.get());
    }

    @Test
    void path_02() {
        final var pathId = UUID.randomUUID().toString();
        Assertions.assertEquals(pathId, case001.get(pathId));
    }

    @Test
    void path_03() {
        final var pathId = UUID.randomUUID().toString();
        Assertions.assertEquals(pathId, case001.get(pathId));
    }
}
