package me.ehp246.test.embedded.log4jcontext;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private AppConfig appConfig;
    @Autowired
    private Log4jContextrCases.Case01 case01;

    @BeforeEach
    void clean() {
        ThreadContext.clearAll();
    }

    @Test
    void test_01() {
        final var expected = UUID.randomUUID().toString();

        final var order = case01.post(expected, new Order(expected, 123));

        Assertions.assertEquals(expected, order.orderId());
    }

    @Test
    void executor_01() throws InterruptedException, ExecutionException {
        final var expectedContext1 = UUID.randomUUID().toString();
        final var expectedContext2 = UUID.randomUUID().toString();

        ThreadContext.put("logger_context_1", expectedContext1);
        ThreadContext.put("logger_context_2", expectedContext2);

        case01.postWithVoidHandler(expectedContext1, new Order(expectedContext1, 123));

        final var map = appConfig.takeResponseContextMap();

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(expectedContext1, map.get("logger_context_1"));
        Assertions.assertEquals(expectedContext2, map.get("logger_context_2"));
    }

    @Test
    void executor_02() throws InterruptedException, ExecutionException {
        final var expectedContext2 = UUID.randomUUID().toString();

        ThreadContext.put("accountId", "1");
        ThreadContext.put("logger_context_2", expectedContext2);

        case01.postWithVoidHandler("2", new Order("12", 123));

        final var map = appConfig.takeResponseContextMap();

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(null, map.get("accountId"));
        Assertions.assertEquals(null, map.get("logger_context_1"));
        Assertions.assertEquals(expectedContext2, map.get("logger_context_2"));
    }

    @Test
    void executor_03() throws InterruptedException, ExecutionException {
        final var context2 = UUID.randomUUID().toString();

        ThreadContext.put("logger_context_2", context2);

        case01.postWithVoidHandlerWithIntrospect("2", new Order(UUID.randomUUID().toString(), 123));

        final var map = appConfig.takeResponseContextMap();

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(null, map.get("logger_context_1"));
        Assertions.assertEquals(context2, map.get("logger_context_2"));

        Assertions.assertEquals(context2, ThreadContext.getContext().get("logger_context_2"), "should be no change");
    }
}
