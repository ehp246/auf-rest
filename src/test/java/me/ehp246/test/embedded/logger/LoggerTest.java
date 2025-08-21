package me.ehp246.test.embedded.logger;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.AufRestOpException;
import me.ehp246.aufrest.api.exception.NotFoundException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class LoggerTest {
    @Autowired
    private LoggerCases.LoggerCase01 case01;

    @Autowired
    private LoggerCases.LoggerCase02 case02;

    @Test
    void test_01() {
        final var now = Instant.now();
        Assertions.assertDoesNotThrow(() -> case01.post(now));
    }

    @Test
    void test_02() {
        final var now = Instant.now();
        Assertions.assertDoesNotThrow(() -> case01.post(now, now.toString()));
    }

    @Test
    void test_03() {
        final var now = Instant.now();
        final var str = now.toString();
        Assertions.assertThrows(UnhandledResponseException.class, () -> case01.postNull(now, str));
    }

    @Test
    void onException_01() {
        final var now = Instant.now();
        Assertions.assertThrows(AufRestOpException.class, () -> case02.post(now));
    }

    @Test
    void onException_02() {
        final var now = Instant.now();
        Assertions.assertThrows(NotFoundException.class, () -> case01.postNullThrowing(now, now.toString()));
    }

}
