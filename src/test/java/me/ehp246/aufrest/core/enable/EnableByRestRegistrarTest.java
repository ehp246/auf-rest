package me.ehp246.aufrest.core.enable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
class EnableByRestRegistrarTest {
    private AnnotationConfigApplicationContext appCtx;

    @AfterEach
    void close() {
        if (appCtx != null) {
            appCtx.close();
            appCtx = null;
        }
    }

    @Test
    void test_001() {
        appCtx = new AnnotationConfigApplicationContext(AppConfigs.Case01.class, Jackson.class);
    }

    @Test
    void test_002() {
        appCtx = new AnnotationConfigApplicationContext(AppConfigs.Case02.class, Jackson.class);
    }
}
