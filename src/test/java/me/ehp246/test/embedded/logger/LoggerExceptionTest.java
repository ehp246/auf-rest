package me.ehp246.test.embedded.logger;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;

import me.ehp246.aufrest.api.exception.AufRestOpException;
import me.ehp246.aufrest.api.rest.RestLogger;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class LoggerExceptionTest {
    @Autowired
    private ApplicationContext appCtx;

    @Autowired
    private LoggerCases.LoggerCase02 case02;

    @Test
    void default_01() {
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> appCtx.getBean(RestLogger.class));
    }

    @Test
    void onException_01() {
        final var now = Instant.now();
        Assertions.assertThrows(AufRestOpException.class, () -> case02.post(now));
    }
}
