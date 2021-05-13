package me.ehp246.aufrest.integration.local.listener;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class ListenerTest {
    @Autowired
    private TestCase001 case001;
    @Autowired
    private List<Listener> reqConsumers;

    @Test
    void listener_001() {
        final var now = Instant.now();

        case001.post(now);

        final var reqConsumer1 = reqConsumers.get(0);
        final var reqConsumer2 = reqConsumers.get(1);

        Assertions.assertEquals(true, reqConsumer1.id() == 1);
        Assertions.assertEquals(true, reqConsumer2.id() == 2);

        Assertions.assertEquals(true, reqConsumer1.httpReq() != null);
        Assertions.assertEquals(true, reqConsumer1.reqByReq() != null);
        Assertions.assertEquals(true, reqConsumer1.httpReq() == reqConsumer2.httpReq());
        Assertions.assertEquals(true, reqConsumer2.httpReq() != null);
        Assertions.assertEquals(true, reqConsumer2.reqByReq() != null);
        Assertions.assertEquals(true, reqConsumer1.reqByReq() == reqConsumer2.reqByReq());
    }
}
