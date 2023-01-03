package me.ehp246.test.app.config01;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Lei Yang
 *
 */
class NameTest {
    private AnnotationConfigApplicationContext appCtx;

    @AfterEach
    void close() {
        if (appCtx != null) {
            appCtx.close();
            appCtx = null;
        }
    }

    @Test
    void name_01() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig01.class);

        Assertions.assertEquals(true, appCtx.getBean(Case01.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(Case01.class.getName()) != null);
    }
}
