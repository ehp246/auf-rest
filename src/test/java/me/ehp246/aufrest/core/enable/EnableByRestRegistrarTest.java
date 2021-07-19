package me.ehp246.aufrest.core.enable;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import me.ehp246.aufrest.api.rest.EnableByRestConfig;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("resource")
class EnableByRestRegistrarTest {

    @Test
    void test_001() {
        final var enableCofig = new AnnotationConfigApplicationContext(AppConfigs.Case01.class, Jackson.class)
                .getBean(EnableByRestConfig.class);

        Assertions.assertEquals(Object.class, enableCofig.errorType());
    }

    @Test
    void test_002() {
        final var enableCofig = new AnnotationConfigApplicationContext(AppConfigs.Case02.class, Jackson.class)
                .getBean(EnableByRestConfig.class);

        Assertions.assertEquals(Instant.class, enableCofig.errorType());
    }
}
