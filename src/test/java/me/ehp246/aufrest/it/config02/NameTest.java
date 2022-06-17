package me.ehp246.aufrest.it.config02;

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
    void name_02() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig02.class);

        Assertions.assertEquals(true, appCtx.getBean(Case02.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(AppConfig02.BEAN_NAME) != null);
    }
}