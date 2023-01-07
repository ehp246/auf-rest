package me.ehp246.aufrest.core.rest.requestbody;

import static org.mockito.Mockito.times;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.BodyHandlerBeanResolver;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.core.rest.ByRestProxyFactory;
import me.ehp246.aufrest.core.rest.DefaultProxyMethodParser;
import me.ehp246.aufrest.core.rest.ProxyMethodParser;
import me.ehp246.test.mock.MockRestFn;

/**
 * @author Lei Yang
 *
 */
class BodyTest {
    private final MockRestFn restFn = new MockRestFn();
    @SuppressWarnings("unchecked")
    private final BodyHandler<Object> resolvedBodyHandler = Mockito.mock(BodyHandler.class);
    @SuppressWarnings("unchecked")
    private final BodyHandler<Object> mockBodyHandler = Mockito.mock(BodyHandler.class);
    private final BodyHandlerBeanResolver bodyHandlerResolver = name -> resolvedBodyHandler;
    private final ProxyMethodParser parser = new DefaultProxyMethodParser(Object::toString, name -> null,
            bodyHandlerResolver, binding -> mockBodyHandler);
    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFn.toProvider(), new ClientConfig(),
            parser);

    @Test
    void request_01() {
        final var expected = Mockito.mock(InputStream.class);

        factory.newInstance(BodyTestCases.RequestCase01.class).get(expected);

        Assertions.assertEquals(true, expected == restFn.req().body());
    }

    @Test
    void request_02() {
        final var payload = UUID.randomUUID().toString();
        final var expected = BodyPublishers.ofString(payload);

        factory.newInstance(BodyTestCases.RequestCase01.class).get(expected);

        Assertions.assertEquals(true, expected == restFn.req().body());
        Assertions.assertEquals(36, ((BodyPublisher) (restFn.req().body())).contentLength());
    }

    @Test
    void request_03() {
        final var expected = BodyPublishers.noBody();

        factory.newInstance(BodyTestCases.RequestCase01.class).get(0, expected, null);

        Assertions.assertEquals(true, expected == restFn.req().body(), "should use the publisher");
    }

    @Test
    void request_04() {
        final var expected = Instant.now();

        factory.newInstance(BodyTestCases.RequestCase01.class).get(expected, -1);

        Assertions.assertEquals(true, expected == restFn.req().body(), "should use the first un-recognized");
    }

    @Test
    void request_05() {
        final var expected = UUID.randomUUID().toString();

        factory.newInstance(BodyTestCases.RequestCase01.class).get(Instant.now(), expected);

        Assertions.assertEquals(true, expected == restFn.req().body(), "should use the annotated");
    }

    @Test
    void request_06() {
        final var expected = Mockito.mock(BodyPublisher.class);

        factory.newInstance(BodyTestCases.RequestCase01.class).get(UUID.randomUUID().toString(), Instant.now(),
                expected);

        Assertions.assertEquals(true, expected == restFn.req().body(), "should use the annotated");
    }

    @Test
    void response_01() {
        final var expected = BodyHandlers.ofByteArray();

        factory.newInstance(BodyTestCases.ResponseCase01.class).getOnMethod(expected);

        Assertions.assertEquals(true, expected == restFn.consumer().handler());
    }

    @Test
    void response_03() {
        factory.newInstance(BodyTestCases.ResponseCase01.class).getOfMapping();

        Assertions.assertEquals(mockBodyHandler, restFn.consumer().handler());
    }

    @Test
    void response_04() {
        final var expected = Mockito.mock(BodyHandler.class);
        final var namedResolver = Mockito.mock(BodyHandlerBeanResolver.class);
        Mockito.when(namedResolver.get(Mockito.eq("named"))).thenReturn(expected);

        new ByRestProxyFactory(restFn.toProvider(), new ClientConfig(),
                new DefaultProxyMethodParser(Object::toString, name -> null, namedResolver,
                        Mockito.mock(InferringBodyHandlerProvider.class))).newInstance(BodyTestCases.ResponseCase01.class)
                                .getOfMappingNamed();

        Assertions.assertEquals(expected, restFn.consumer().handler());
    }

    @Test
    void response_05() {
        final var expected = Mockito.mock(BodyHandler.class);
        final var namedResolver = Mockito.mock(BodyHandlerBeanResolver.class);

        new ByRestProxyFactory(restFn.toProvider(), new ClientConfig(),
                new DefaultProxyMethodParser(Object::toString, name -> null, namedResolver,
                        Mockito.mock(InferringBodyHandlerProvider.class))).newInstance(BodyTestCases.ResponseCase02.class)
                                .getOnMethod(0, expected);

        /*
         * Should not call the resolver
         */
        Mockito.verify(namedResolver, times(0)).get(Mockito.anyString());
        Assertions.assertEquals(expected, restFn.consumer().handler());
    }

    @Test
    void response_06() {
        final var namedResolver = Mockito.mock(BodyHandlerBeanResolver.class);

        new ByRestProxyFactory(restFn.toProvider(), new ClientConfig(),
                new DefaultProxyMethodParser(Object::toString, name -> null, namedResolver,
                        Mockito.mock(InferringBodyHandlerProvider.class))).newInstance(BodyTestCases.ResponseCase02.class)
                                .get(null);

        Assertions.assertEquals(null, restFn.consumer().handler());
    }

    @Test
    void response_07() {
        final var expected = Mockito.mock(BodyHandler.class);
        final var namedResolver = Mockito.mock(BodyHandlerBeanResolver.class);
        Mockito.when(namedResolver.get(Mockito.eq("interfaceNamed"))).thenReturn(expected);

        new ByRestProxyFactory(restFn.toProvider(), new ClientConfig(), new DefaultProxyMethodParser(Object::toString,
                name -> null, namedResolver,
                Mockito.mock(InferringBodyHandlerProvider.class))).newInstance(BodyTestCases.ResponseCase02.class).getOfMapping();

        Assertions.assertEquals(expected, restFn.consumer().handler());
    }

    @Test
    void response_08() {
        final var expected = Mockito.mock(BodyHandler.class);
        final var namedResolver = Mockito.mock(BodyHandlerBeanResolver.class);
        Mockito.when(namedResolver.get(Mockito.eq("methodNamed"))).thenReturn(expected);

        new ByRestProxyFactory(restFn.toProvider(), new ClientConfig(),
                new DefaultProxyMethodParser(Object::toString, name -> null, namedResolver,
                        Mockito.mock(InferringBodyHandlerProvider.class))).newInstance(BodyTestCases.ResponseCase02.class)
                                .getOfMappingNamed();

        Assertions.assertEquals(expected, restFn.consumer().handler());
    }
}
