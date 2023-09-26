package me.ehp246.test.embedded.logger;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.NotFoundException;
import me.ehp246.aufrest.api.exception.RestFnException;
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
        case01.post(Instant.now());
    }

    @Test
    void test_02() {
        case01.post(Instant.now(), Instant.now().toString());
    }

    @Test
    void test_03() {
        Assertions.assertThrows(UnhandledResponseException.class,
                () -> case01.postNull(Instant.now(), Instant.now().toString()));
    }

    @Test
    void onException_01() {
        Assertions.assertThrows(RestFnException.class, () -> case02.post(Instant.now()));
    }

    @Test
    void onException_02() {
        Assertions.assertThrows(NotFoundException.class,
                () -> case01.postNullThrowing(Instant.now(), Instant.now().toString()));
    }

}
