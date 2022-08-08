package me.ehp246.aufrest.core.byrest.conneg;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;
import me.ehp246.aufrest.core.byrest.DefaultProxyMethodParser;

/**
 * @author Lei Yang
 *
 */
class ContentTypeTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final RestFn restFn = request -> {
        reqRef.set(request);
        return Mockito.mock(HttpResponse.class);
    };
    private final RestFnProvider restFnProvider = cfg -> restFn;
    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFnProvider, new ClientConfig(),
            new DefaultProxyMethodParser(Object::toString, name -> null, name -> BodyHandlers.discarding(),
                    binding -> BodyHandlers.discarding()));

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void test001() {
        factory.newInstance(TestCase001.class).get();

        Assertions.assertEquals("application/json", reqRef.get().accept());
        Assertions.assertEquals("", reqRef.get().contentType());
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
