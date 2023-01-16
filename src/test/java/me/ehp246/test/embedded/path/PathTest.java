package me.ehp246.test.embedded.path;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.path.TestCases.Case01;
import me.ehp246.test.embedded.path.TestCases.Case02;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class PathTest {
    @Autowired
    private Case01 case01;
    @Autowired
    private Case02 case02;

    @Test
    void path_01() {
        Assertions.assertEquals("/path", case01.get());
    }

    @Test
    void path_02() {
        final var pathId = UUID.randomUUID().toString();
        Assertions.assertEquals(pathId, case01.get(pathId));
    }

    @Test
    void path_03() {
        final var pathId = UUID.randomUUID().toString();
        Assertions.assertEquals(pathId, case01.get(pathId));
    }

    @Test
    void path_04() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> case02.get(UUID.randomUUID().toString()));
    }

    @Test
    void path_05() {
        final var pathId = UUID.randomUUID().toString();

        Assertions.assertEquals(pathId, case02.get(pathId, "path"));
    }
}
