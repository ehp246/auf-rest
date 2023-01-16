package me.ehp246.test.app.beanname.postive;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.test.app.beanname.postive.TestCases.Case02;

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
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config01.class);

        Assertions.assertEquals(true, appCtx.getBean(TestCases.Case01.class) != null);
        Assertions.assertEquals(true, appCtx.getBean("case01") instanceof TestCases.Case01);

        Assertions.assertEquals(true, appCtx.getBean(Case02.class) != null);
        Assertions.assertEquals(true, appCtx.getBean("30f0b393-0a64-4b75-a5d8-2737cba10508") instanceof Case02);
    }

}
