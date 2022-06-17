package me.ehp246.aufrest.it.config04;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Lei Yang
 *
 */
@Disabled
class PropertyTest {
    private AnnotationConfigApplicationContext appCtx;

    @AfterEach
    void close() {
        if (appCtx != null) {
            appCtx.close();
            appCtx = null;
        }
    }

    @Test
    void property_01() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config01.class);

        Assertions.assertThrows(BeanCreationException.class, () -> appCtx.getBean(AppConfig.Config01.Case01.class));
    }
}
