package me.ehp246.aufrest.core.byrest.returntype;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;

/**
 * @author Lei Yang
 *
 */
class ReturnTypeTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();
    private final RestFn client = request -> {
        reqRef.set(request);
        return Mockito.mock(HttpResponse.class);
    };
    private final ByRestProxyFactory factory = new ByRestProxyFactory(cfg -> client,
            new MockEnvironment()::resolveRequiredPlaceholders);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void return_type_001() {
        Assertions.assertThrows(IllegalArgumentException.class,
                factory.newInstance(ReturnTypeCase001.class)::get001);
    }

    @Test
    void return_type_002() {
        Assertions.assertThrows(IllegalArgumentException.class,
                factory.newInstance(ReturnTypeCase001.class)::get002);
    }

    @Test
    void return_type_003() {
        Assertions.assertThrows(Exception.class, factory.newInstance(ReturnTypeCase001.class)::get004);
    }

    @Test
    void return_type_004() {
        Assertions.assertThrows(Exception.class, factory.newInstance(ReturnTypeCase001.class)::get005);
    }
}
