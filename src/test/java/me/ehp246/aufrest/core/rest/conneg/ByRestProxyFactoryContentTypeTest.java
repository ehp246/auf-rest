package me.ehp246.aufrest.core.rest.conneg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;
import me.ehp246.test.mock.MockRestFn;

/**
 * @author Lei Yang
 *
 */
class ByRestProxyFactoryContentTypeTest {
    private final MockRestFn mockRestFn = new MockRestFn();
    private final ByRestProxyFactory factory = new ByRestProxyFactory(mockRestFn.toProvider(), new ClientConfig(),
            new DefaultProxyMethodParser(Object::toString, name -> null, name -> r -> null,
                    binding -> r -> null));

    @Test
    void test001() {
        factory.newInstance(TestCase001.class).get();

        Assertions.assertEquals("application/json", mockRestFn.req().accept());
        Assertions.assertEquals("", mockRestFn.req().contentType());
    }

    @Test
    void test002() {
        factory.newInstance(TestCase001.class).put();

        Assertions.assertEquals("text/plain", mockRestFn.req().accept());
        Assertions.assertEquals("text/plain", mockRestFn.req().contentType());
    }

    @Test
    void test003() {
        factory.newInstance(TestCase001.class).post();

        Assertions.assertEquals("i accept", mockRestFn.req().accept());
        Assertions.assertEquals("i produce", mockRestFn.req().contentType());
    }
}
