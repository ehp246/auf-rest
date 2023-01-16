package me.ehp246.test.embedded.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class CacheTest {
    @Autowired
    private CacheCase cacheCase;

    @Test
    void inc_01() {
        Assertions.assertEquals(1, cacheCase.postInc());
        Assertions.assertEquals(1, cacheCase.postInc());
    }
}
