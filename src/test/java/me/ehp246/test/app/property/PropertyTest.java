package me.ehp246.test.app.property;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.HttpClientBuilderSupplier;
import me.ehp246.aufrest.api.rest.RestLogger;

/**
 * @author Lei Yang
 *
 */
class PropertyTest {

    @Test
    void connectTimeout_01() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("me.ehp246.aufrest.connect-timeout", "123"));
        appCtx.register(AppConfig.class);

        final var e = Assertions.assertThrows(BeanCreationException.class, appCtx::refresh);

        Assertions.assertEquals(BeanInstantiationException.class, e.getCause().getCause().getClass());

        appCtx.close();
    }

    @Test
    void connectTimeout_02() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("me.ehp246.aufrest.connect-timeout", " "));
        appCtx.register(AppConfig.class);

        appCtx.refresh();

        Assertions.assertEquals(true,
                appCtx.getBean(HttpClientBuilderSupplier.class).get().build().connectTimeout().isEmpty());

        appCtx.close();
    }

    @Test
    void connectTimeout_03() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment().withProperty("me.ehp246.aufrest.connect-timeout", "PT1M"));
        appCtx.register(AppConfig.class);

        appCtx.refresh();

        Assertions.assertEquals(1,
                appCtx.getBean(HttpClientBuilderSupplier.class).get().build().connectTimeout().get().toMinutes());

        appCtx.close();
    }

    @Test
    void restlogger_01() {
        final var appCtx = new AnnotationConfigApplicationContext();
        appCtx.setEnvironment(new MockEnvironment());
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> appCtx.getBean(RestLogger.class));

        appCtx.close();
    }

    @Test
    void restlogger_02() {
        final var appCtx = new AnnotationConfigApplicationContext();
        // Should be hard coded.
        appCtx.setEnvironment(new MockEnvironment().withProperty("me.ehp246.aufrest.restlogger.enabled", "true"));
        appCtx.register(AppConfig.class);
        appCtx.refresh();

        Assertions.assertNotNull(appCtx.getBean(RestLogger.class));

        appCtx.close();
    }
}
