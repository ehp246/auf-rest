package me.ehp246.aufrest.api.configuration;

import java.net.http.HttpRequest.BodyPublisher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.BodyReceiver;
import me.ehp246.aufrest.api.spi.JsonFn;
import me.ehp246.aufrest.mock.MockReq;

/**
 * @author Lei Yang
 *
 */
class DefaultBodyPublisherProviderTest {
    private final JsonFn jsonFn = new JsonFn() {

        @Override
        public String toJson(Object value) {
            return null;
        }

        @Override
        public Object fromJson(String json, BodyReceiver receiver) {
            return null;
        }
    };

    @Test
    void body_01() {
        Assertions.assertEquals(0,
                new DefaultBodyPublisherProvider(jsonFn).get(MockReq.withBody(null)).contentLength());
    }

    @Test
    void body_02() {
        final var mock = Mockito.mock(BodyPublisher.class);

        Assertions.assertEquals(mock, new DefaultBodyPublisherProvider(jsonFn).get(MockReq.withBody(mock)));
    }
}
