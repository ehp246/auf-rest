package me.ehp246.aufrest.core.byrest.conneg;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;
import me.ehp246.aufrest.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
class ContentTypeTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> request -> {
        reqRef.set(request);
        return new MockHttpResponse<Object>();
    }, new MockEnvironment()::resolveRequiredPlaceholders);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void test001() {
        factory.newInstance(TestCase001.class).get();

        Assertions.assertEquals("application/json", reqRef.get().accept());
        Assertions.assertEquals("application/json", reqRef.get().contentType());
    }

    @Test
    void test002() {
        factory.newInstance(TestCase001.class).put();

        Assertions.assertEquals("text/plain", reqRef.get().accept());
        Assertions.assertEquals("text/plain", reqRef.get().contentType());
    }

    @Test
    void test003() {
        factory.newInstance(TestCase001.class).post();

        Assertions.assertEquals("i accept", reqRef.get().accept());
        Assertions.assertEquals("i produce", reqRef.get().contentType());
    }
}
