package me.ehp246.aufrest.integration.local.logger;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restLogger=true", "me.ehp246.aufrest.restLogger.maskedHeaders=" })
class LoggerHeaders2Test {
    @Autowired
    private LoggerCases.LoggerCase01 case01;

    @Test
    void test_01() {
        case01.post(Instant.now());
    }

    @Test
    void test_02() {
        // should show everything.
        case01.post(Instant.now(), Instant.now().toString());
    }
}
