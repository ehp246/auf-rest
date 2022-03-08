package me.ehp246.aufrest.core.byrest.requestbody;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
class RequestBodyTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final ByRestFactory factory = new ByRestFactory(cfg -> request -> {
        reqRef.set(request);
        return new MockHttpResponse<Object>();
    }, new MockEnvironment()::resolveRequiredPlaceholders);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void body_01() {
        final var body = Mockito.mock(InputStream.class);

        factory.newInstance(RequestBodyTestCase01.class).get(body);

        Assertions.assertEquals(body, reqRef.get().body());
    }

    @Test
    void body_02() {
        final var payload = UUID.randomUUID().toString();
        final var body = BodyPublishers.ofString(payload);

        factory.newInstance(RequestBodyTestCase01.class).get(body);

        Assertions.assertEquals(36, ((BodyPublisher) (reqRef.get().body())).contentLength());
    }

    @Test
    void body_03() {
        final var noBody = BodyPublishers.noBody();

        factory.newInstance(RequestBodyTestCase01.class).get(0, noBody, null);

        Assertions.assertEquals(noBody, reqRef.get().body(), "should use the publisher");
    }
}
