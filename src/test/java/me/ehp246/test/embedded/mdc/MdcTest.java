package me.ehp246.test.embedded.mdc;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.test.embedded.mdc.MdcCases.Order;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = { "me.ehp246.aufrest.restlogger.enabled=true" })
class MdcTest {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private MdcCases.Case01 case01;

    @BeforeEach
    void clean() {
        MDC.clear();
        appConfig.reset();
    }

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();

        final var order = case01.post(expected, new Order(expected, 123));

        final var request = appConfig.takeRequest();
        final var requestContextMap = appConfig.takeRequestContextMap();

        Assertions.assertEquals(expected, order.orderId());
        Assertions.assertEquals(request.id(), requestContextMap.get("AufRestRequestId"));
        Assertions.assertEquals(null, MDC.get("AufRestRequestId"));
    }

    @Test
    void executor_01() throws InterruptedException, ExecutionException {
        final var expectedContext1 = UUID.randomUUID().toString();
        final var expectedContext2 = UUID.randomUUID().toString();

        MDC.put("logger_context_1", expectedContext1);
        MDC.put("logger_context_2", expectedContext2);

        case01.postWithVoidHandler(expectedContext1, new Order(expectedContext1, 123));

        final var request = appConfig.takeRequest();
        final var map = appConfig.takeResponseContextMap();

        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals(expectedContext1, map.get("logger_context_1"));
        Assertions.assertEquals(expectedContext2, map.get("logger_context_2"));
        Assertions.assertEquals(request.id(), map.get("AufRestRequestId"));
    }

    @Test
    void executor_02() throws InterruptedException, ExecutionException {
        final var expectedContext2 = UUID.randomUUID().toString();

        MDC.put("accountId", "1");
        MDC.put("logger_context_2", expectedContext2);

        case01.postWithVoidHandler("2", new Order("12", 123));

        final var map = appConfig.takeResponseContextMap();

        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals(null, map.get("accountId"));
        Assertions.assertEquals(null, map.get("logger_context_1"));
        Assertions.assertEquals(expectedContext2, map.get("logger_context_2"));
    }

    @Test
    void executor_03() throws InterruptedException, ExecutionException {
        final var context2 = UUID.randomUUID().toString();

        MDC.put("logger_context_2", context2);

        case01.postWithVoidHandlerWithIntrospect("2", new Order(UUID.randomUUID().toString(), 123));

        final var map = appConfig.takeResponseContextMap();

        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals(null, map.get("logger_context_1"));
        Assertions.assertEquals(context2, map.get("logger_context_2"));

        Assertions.assertEquals(context2, ThreadContext.getContext().get("logger_context_2"),
                "should be no change");
    }
}
