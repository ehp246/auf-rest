package me.ehp246.aufrest.core.rest.returntype;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;
import me.ehp246.test.mock.MockBodyHandlerProvider;
import me.ehp246.test.mock.MockRestFn;

/**
 * @author Lei Yang
 *
 */
class ReturnTypeTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();
    private final MockRestFn restFn = new MockRestFn();
    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFn.toProvider(), new DefaultProxyMethodParser(
            Object::toString, name -> null, name -> r -> null, new MockBodyHandlerProvider()));

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void return_type_01() {
        Assertions.assertThrows(UnsupportedOperationException.class, factory.newInstance(ReturnTypeCase01.class)::get02,
                "should not have raw type");
    }

    @Test
    void return_type_02() {
        Assertions.assertThrows(UnsupportedOperationException.class, factory.newInstance(ReturnTypeCase01.class)::get01,
                "should specify the reponse body type");
    }
}
