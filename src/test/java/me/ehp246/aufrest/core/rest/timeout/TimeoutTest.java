package me.ehp246.aufrest.core.rest.timeout;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;
import me.ehp246.test.mock.MockBodyHandlerProvider;
import me.ehp246.test.mock.MockRestFn;

/**
 * @author Lei Yang
 *
 */
class TimeoutTest {
    private final MockRestFn restFn = new MockRestFn();
    private final PropertyResolver env = new MockEnvironment().withProperty("api.timeout.5s", "PT5S")
            .withProperty("api.timeout.illegal", "5")::resolveRequiredPlaceholders;

    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> restFn,
            new DefaultProxyMethodParser(env, name -> null, name -> r -> null, new MockBodyHandlerProvider()));

    @Test
    void timeout_01() {
        factory.newInstance(TestCase001.class).get();

        Assertions.assertEquals(null, restFn.req().timeout());
    }

    @Test
    void timeout_02() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase002.class).get());
    }

    @Test
    void timeout_03() {
        factory.newInstance(TestCase003.class).get();

        Assertions.assertEquals(11021, restFn.req().timeout().toMillis());
    }

    @Test
    void timeout_04() {
        factory.newInstance(TestCase004.class).get();

        Assertions.assertEquals(5, restFn.req().timeout().toSeconds());
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

        Assertions.assertEquals(10, restFn.req().timeout().toMillis());
    }
}
