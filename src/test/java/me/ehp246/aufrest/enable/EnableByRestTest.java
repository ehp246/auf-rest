package me.ehp246.aufrest.enable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufrest.enable.config01.AppConfig01;
import me.ehp246.aufrest.enable.config01.Case01;
import me.ehp246.aufrest.enable.config02.AppConfig02;
import me.ehp246.aufrest.enable.config02.Case02;
import me.ehp246.aufrest.enable.config03.Case03;

/**
 * @author Lei Yang
 *
 */
class EnableByRestTest {
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

    @Test
    void name_02() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig02.class);

        Assertions.assertEquals(true, appCtx.getBean(Case02.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(AppConfig02.BEAN_NAME) != null);
    }

    @Test
    void scan_03() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config01.class);

        Assertions.assertEquals(true, appCtx.getBean(Case01.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(Case01.class.getName()) != null);

        Assertions.assertEquals(true, appCtx.getBean(Case02.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(AppConfig02.BEAN_NAME) != null);
    }

    @Test
    void scan_04() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config02.class);

        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> appCtx.getBean(Case01.class));

        Assertions.assertEquals(true, appCtx.getBean(Case02.class) != null);
        Assertions.assertEquals(true, appCtx.getBean(AppConfig02.BEAN_NAME) != null);

        Assertions.assertEquals(true, appCtx.getBean(Case03.class) != null);

    }
}
