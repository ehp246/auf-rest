package me.ehp246.test.embedded.listener;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class ListenerTest {
    @Autowired
    private TestCase case1;
    @Autowired
    private List<Listener> listeners;

    @BeforeEach
    void clear() {
        listeners.stream().forEach(Listener::clear);
    }

    @Test
    void listener_001() {
        final var now = Instant.now();

        case1.post(now);

        final var reqConsumer1 = listeners.get(0);
        final var reqConsumer2 = listeners.get(1);

        Assertions.assertEquals(true, reqConsumer1.id() == 1);
        Assertions.assertEquals(true, reqConsumer2.id() == 2);

        Assertions.assertEquals(true, reqConsumer1.httpReq() != null);
        Assertions.assertEquals(true, reqConsumer1.reqByReq() != null);
        Assertions.assertEquals(true, reqConsumer1.httpReq() == reqConsumer2.httpReq());
        Assertions.assertEquals(true, reqConsumer2.httpReq() != null);
        Assertions.assertEquals(true, reqConsumer2.reqByReq() != null);
        Assertions.assertEquals(true, reqConsumer1.reqByReq() == reqConsumer2.reqByReq());
    }

    @Test
    void errorResponse_01() {
        Assertions.assertThrows(UnhandledResponseException.class, this.case1::get);

        final var set = listeners.stream().map(listener -> {
            Assertions.assertEquals(true, listener.getHttpResponse() != null);
            return listener;
        }).map(Listener::getHttpResponse).collect(Collectors.toSet());

        Assertions.assertEquals(1, set.size());
        Assertions.assertEquals(405, set.stream().findAny().get().statusCode());
    }
}
