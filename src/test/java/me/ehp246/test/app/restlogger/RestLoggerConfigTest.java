package me.ehp246.test.app.restlogger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestLogger;

/**
 * @author Lei Yang
 *
 */
class RestLoggerConfigTest {
    @Test
    void off_01() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment());
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> appCtx.getBean(RestLogger.class));

        appCtx.close();
    }

    @Test
    void on_01() {
        final var appCtx = new AnnotationConfigApplicationContext();
        // Should be hard coded.
        appCtx.setEnvironment(new MockEnvironment().withProperty("me.ehp246.aufrest.restlogger.enabled", "true"));
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertNotNull(appCtx.getBean(RestLogger.class));

        appCtx.close();
    }
}
