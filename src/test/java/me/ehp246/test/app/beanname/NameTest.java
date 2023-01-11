package me.ehp246.test.app.beanname;

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
        appCtx = new AnnotationConfigApplicationContext(AppConfig02.class);

        Assertions.assertEquals(true, appCtx.getBean(Case01.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(Case01.class.getName()) != null);
    }

    @Test
    void name_02() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig02.class);

        Assertions.assertEquals(true, appCtx.getBean(Case02.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(AppConfig02.BEAN_NAME) != null);
    }
}
