package me.ehp246.aufrest.api.configuration;

import java.net.http.HttpRequest.BodyPublisher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.mock.MockReq;

/**
 * @author Lei Yang
 *
 */
class DefaultBodyPublisherProviderTest {

    @Test
    void body_01() {
        Assertions.assertEquals(0,
                new DefaultBodyPublisherProvider(v -> null).get(MockReq.withBody(null)).contentLength());
    }

    @Test
    void body_02() {
        final var mock = Mockito.mock(BodyPublisher.class);

        Assertions.assertEquals(mock, new DefaultBodyPublisherProvider(v -> null).get(MockReq.withBody(mock)));
    }
}
