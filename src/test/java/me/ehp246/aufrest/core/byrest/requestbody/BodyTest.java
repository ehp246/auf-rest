package me.ehp246.aufrest.core.byrest.requestbody;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;
import me.ehp246.aufrest.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
class BodyTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();
    private final RestFnProvider restFnProvider = cfg -> request -> {
        reqRef.set(request);
        return new MockHttpResponse<Object>();
    };

    private final ByRestProxyFactory factory = new ByRestProxyFactory(restFnProvider,
            new MockEnvironment()::resolveRequiredPlaceholders);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void request_01() {
        final var body = Mockito.mock(InputStream.class);

        factory.newInstance(BodyTestCases.RequestCase01.class).get(body);

        Assertions.assertEquals(body, reqRef.get().body());
    }

    @Test
    void request_02() {
        final var payload = UUID.randomUUID().toString();
        final var body = BodyPublishers.ofString(payload);

        factory.newInstance(BodyTestCases.RequestCase01.class).get(body);

        Assertions.assertEquals(36, ((BodyPublisher) (reqRef.get().body())).contentLength());
    }

    @Test
    void request_03() {
        final var noBody = BodyPublishers.noBody();

        factory.newInstance(BodyTestCases.RequestCase01.class).get(0, noBody, null);

        Assertions.assertEquals(noBody, reqRef.get().body(), "should use the publisher");
    }

    @Test
    void response_01() {
        final var handler = BodyHandlers.ofByteArray();
        factory.newInstance(BodyTestCases.ResponseCase01.class).getOnMethod(handler);

        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }

    @Test
    void response_02() {
        final var nameRef = new String[] { "1" };
        final var handler = BodyHandlers.ofByteArray();

        new ByRestProxyFactory(restFnProvider, new RestClientConfig(), new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase01.class)
                        .getOfMapping(handler);

        Assertions.assertEquals("1", nameRef[0], "should not call the resolver");
        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }

    @Test
    void response_03() {
        final var nameRef = new String[1];
        new ByRestProxyFactory(restFnProvider, new RestClientConfig(), new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase01.class).getOfMapping();

        Assertions.assertEquals(null, nameRef[0]);
        Assertions.assertEquals(true, reqRef.get().responseBodyHandler() != null);
    }

    @Test
    void response_04() {
        final var handler = Mockito.mock(BodyHandler.class);

        final var nameRef = new String[1];

        new ByRestProxyFactory(restFnProvider, new RestClientConfig(),
                new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase01.class)
                        .getOfMappingNamed();

        Assertions.assertEquals("named", nameRef[0]);
        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }

    @Test
    void response_05() {
        final var handler = Mockito.mock(BodyHandler.class);

        final var nameRef = new String[1];

        new ByRestProxyFactory(restFnProvider, new RestClientConfig(), new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase02.class).getOnMethod(0,
                        handler);

        Assertions.assertEquals(null, nameRef[0]);
        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }

    @Test
    void response_06() {
        final var handler = Mockito.mock(BodyHandler.class);

        final var nameRef = new String[1];

        new ByRestProxyFactory(restFnProvider, new RestClientConfig(), new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase02.class)
                        .getOfMapping(null);

        Assertions.assertEquals("interfaceNamed", nameRef[0], "should fall back to annotations");
        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }

    @Test
    void response_07() {
        final var handler = Mockito.mock(BodyHandler.class);

        final var nameRef = new String[1];

        new ByRestProxyFactory(restFnProvider, new RestClientConfig(), new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase02.class).getOfMapping();

        Assertions.assertEquals("interfaceNamed", nameRef[0]);
        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }

    @Test
    void response_08() {
        final var handler = Mockito.mock(BodyHandler.class);

        final var nameRef = new String[1];

        new ByRestProxyFactory(restFnProvider, new RestClientConfig(), new MockEnvironment()::resolveRequiredPlaceholders,
                binding -> BodyHandlers.discarding()).newInstance(BodyTestCases.ResponseCase02.class)
                        .getOfMappingNamed();

        Assertions.assertEquals("methodNamed", nameRef[0]);
        Assertions.assertEquals(handler, reqRef.get().responseBodyHandler());
    }
}
