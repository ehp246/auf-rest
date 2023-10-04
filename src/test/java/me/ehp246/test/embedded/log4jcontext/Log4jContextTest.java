package me.ehp246.test.embedded.log4jcontext;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.log4jcontext.Log4jContextrCases.Order;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class Log4jContextTest {
    @Autowired
    private Log4jContextrCases.Case01 case01;

    @Test
    void test_01() {
        final var expected = UUID.randomUUID().toString();

        final var order = case01.post(expected, new Order(expected, 123));

        Assertions.assertEquals(expected, order.orderId());
    }
}
