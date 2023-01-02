package me.ehp246.aufrest.core.rest.timeout;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;

/**
 * @author Lei Yang
 *
 */
class TimeoutTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final RestFn restFn = (req, con) -> {
        reqRef.set(req);
        return Mockito.mock(HttpResponse.class);
    };
    private final PropertyResolver env = new MockEnvironment().withProperty("api.timeout.5s", "PT5S")
            .withProperty("api.timeout.illegal", "5")::resolveRequiredPlaceholders;

    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> restFn, new ClientConfig(),
            new DefaultProxyMethodParser(env, name -> null, name -> r -> null, binding -> r -> null));

    @Test
    void timeout_01() {
        factory.newInstance(TestCase001.class).get();

        Assertions.assertEquals(null, reqRef.get().timeout());
    }

    @Test
    void timeout_02() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase002.class).get());
    }

    @Test
    void timeout_03() {
        factory.newInstance(TestCase003.class).get();

        Assertions.assertEquals(11021, reqRef.get().timeout().toMillis());
    }

    @Test
    void timeout_04() {
        factory.newInstance(TestCase004.class).get();

        Assertions.assertEquals(5, reqRef.get().timeout().toSeconds());
    }

    @Test
    void timeout_05() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase05.class).get());
    }

    @Test
    void timeout_06() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase06.class).get());
    }

    @Test
    void timeout_07() {
        factory.newInstance(TestCase007.class).get();

        Assertions.assertEquals(10, reqRef.get().timeout().toMillis());
    }
}
