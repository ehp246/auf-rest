package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.exception.BadGatewayException;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.exception.ServerErrorException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.RestFnConfig;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;
import me.ehp246.test.mock.MockBodyHandler;
import me.ehp246.test.mock.MockHttpRequestBuilder;
import me.ehp246.test.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
class DefaultRestFnProviderTest {
    private final static MockHttpRequestBuilder requestBuilder = new MockHttpRequestBuilder();
    private final static MockBodyHandler<Object> handlerProvider = new MockBodyHandler<>(null);

    /**
     * For each get call on the client provider, the provider should ask
     * client-builder supplier for a new builder. It should not re-use
     * previous-acquired builder.
     */
    @Test
    void client_builder_001() {
        final var client = new MockClientBuilderSupplier();
        final var clientProvider = new DefaultRestFnProvider(requestBuilder, handlerProvider.toProvider(),
                client::builder, null, null);
        final var count = (int) (Math.random() * 20);

        IntStream.range(0, count).forEach(i -> clientProvider.get(new RestFnConfig()));

        Assertions.assertEquals(count, client.builderCount(), "Should ask for a new builder for each client");
    }

    @Test
    void timeout_global_connect_001() {
        final HttpClient.Builder mockBuilder = Mockito.mock(HttpClient.Builder.class);
        final var ref = new AtomicReference<Duration>();

        Mockito.when(mockBuilder.connectTimeout(Mockito.any())).thenAnswer(invocation -> {
            ref.set(invocation.getArgument(0));
            return mockBuilder;
        });

        new DefaultRestFnProvider(requestBuilder, handlerProvider.toProvider(), () -> mockBuilder, null, null)
                .get(new RestFnConfig());

        Assertions.assertEquals(null, ref.get());
    }

    @Test
    void listener_001() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";

        final var map = new HashMap<>();
        final var clientBuilderSupplier = new MockClientBuilderSupplier();

        final var obs = List.of(new RestListener() {

            @Override
            public void onRequest(final HttpRequest httpRequest, final RestRequest req) {
                map.put("1", httpRequest);
                map.put("2", req);
            }

        }, new RestListener() {

            @Override
            public void onRequest(final HttpRequest httpRequest, final RestRequest req) {
                map.put("3", httpRequest);
                map.put("4", req);
            }
        });

        new DefaultRestFnProvider(reqBuilder, handlerProvider.toProvider(), clientBuilderSupplier::builder, obs, null)
                .get(new RestFnConfig()).applyForResponse(req);

        Assertions.assertEquals(true, map.get("1") == mockedReq);
        Assertions.assertEquals(true, map.get("1") == map.get("3"));
        Assertions.assertEquals(true, map.get("2") == map.get("4"));
    }

    @Test
    void listener_exception_02() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";

        final var map = new HashMap<>();
        final var orig = new RuntimeException("Should not be given to the listeners.");

        Exception ex = null;
        try {
            new DefaultRestFnProvider(reqBuilder, handlerProvider.toProvider(),
                    new MockClientBuilderSupplier(orig)::builder, List.of(new RestListener() {

                        @Override
                        public void onException(final Exception exception, final HttpRequest httpRequest,
                                final RestRequest req) {
                            map.put("1", exception);
                        }
                    }), null).get(new RestFnConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        // The exception happens outside of send.
        Assertions.assertEquals(true, map.get("1") == null, "should not have it");
        Assertions.assertEquals(true, ex == orig);
    }

    @Test
    void listener_003() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";

        final var map = new HashMap<>();
        final var orig = new IOException("This is a test");

        Exception ex = null;
        try {
            new DefaultRestFnProvider(reqBuilder, handlerProvider.toProvider(),
                    new MockClientBuilderSupplier(orig)::builder, List.of(new RestListener() {

                        @Override
                        public void onException(final Exception exception, final HttpRequest httpRequest,
                                final RestRequest req) {
                            map.put("5", exception);
                        }
                    }), null).get(new RestFnConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        Assertions.assertEquals(true, map.get("5") == orig, "should just be caught by Fn");
        Assertions.assertEquals(true, ex.getCause() == orig);
    }

    @Test
    void listener_onException_01() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";

        final var map = new HashMap<>();
        final var le = new RuntimeException();
        final var orig = new IOException("This is a test");

        Exception ex = null;
        try {
            new DefaultRestFnProvider(reqBuilder, handlerProvider.toProvider(),
                    new MockClientBuilderSupplier(orig)::builder, List.of(new RestListener() {

                        @Override
                        public void onException(final Exception exception, final HttpRequest httpRequest,
                                final RestRequest req) {
                            throw le;
                        }
                    }, new RestListener() {

                        @Override
                        public void onException(final Exception exception, final HttpRequest httpRequest,
                                final RestRequest req) {
                            map.put("5", exception);
                        }
                    }), null).get(new RestFnConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        Assertions.assertEquals(true, map.get("5") == null, "should be skipped");
        Assertions.assertEquals(true, ex.getCause().getSuppressed()[0] == le, "should have the suppressed");
    }

    @Test
    void listener_004() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";
        final var orig = new IllegalArgumentException("This is a test");

        Exception ex = null;
        try {
            new DefaultRestFnProvider(reqBuilder, handlerProvider.toProvider(),
                    new MockClientBuilderSupplier()::builder, List.of(new RestListener() {

                        @Override
                        public void onRequest(final HttpRequest httpRequest, final RestRequest req) {
                            throw orig;
                        }
                    }), null).get(new RestFnConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        Assertions.assertEquals(true, ex == orig, "should have no wrap");
    }

    @Test
    void exception_01() {
        final var toBeThrown = new IOException();

        final var ex = Assertions.assertThrows(RestFnException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        new MockClientBuilderSupplier(toBeThrown)::builder, null, null).get(new RestFnConfig())
                        .applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(true, ex.getCause() == toBeThrown);
    }

    @Test
    void exception_02() {
        final var toBeThrown = new InterruptedException();

        final var ex = Assertions.assertThrows(RestFnException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        new MockClientBuilderSupplier(toBeThrown)::builder, null, null).get(new RestFnConfig())
                        .applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(true, ex.getCause() == toBeThrown);
    }

    @Test
    void exception_03() {
        final RestRequest req = () -> "http://nowhere";
        final var response = new MockHttpResponse<>(600);

        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(response), null, null).get(new RestFnConfig())
                        .applyForResponse(req));

        Assertions.assertEquals(600, ex.getCause().statusCode());
    }

    @Test
    void exception_04() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(new MockHttpResponse<>(502)), null, null)
                        .get(new RestFnConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(BadGatewayException.class, ex.getCause().getClass());
        Assertions.assertEquals(502, ex.getCause().statusCode());
    }

    @Test
    void exception_05() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(new MockHttpResponse<>(503)), null, null)
                        .get(new RestFnConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(ServiceUnavailableException.class, ex.getCause().getClass());
        Assertions.assertEquals(503, ex.getCause().statusCode());
    }

    @Test
    void exception_06() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(new MockHttpResponse<>(504)), null, null)
                        .get(new RestFnConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(GatewayTimeoutException.class, ex.getCause().getClass());
        Assertions.assertEquals(504, ex.getCause().statusCode());
    }

    @Test
    void exception_07() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(new MockHttpResponse<>(590)), null, null)
                        .get(new RestFnConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(ServerErrorException.class, ex.getCause().getClass());
        Assertions.assertEquals(590, ex.getCause().statusCode());
    }

    @Test
    void exception_08() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(new MockHttpResponse<>(439)), null, null)
                        .get(new RestFnConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(ClientErrorException.class, ex.getCause().getClass());
        Assertions.assertEquals(439, ex.getCause().statusCode());
    }

    @Test
    void exception_09() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(new MockHttpResponse<>(300)), null, null)
                        .get(new RestFnConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(RedirectionException.class, ex.getCause().getClass());
        Assertions.assertEquals(300, ex.getCause().statusCode());
    }

    @Test
    void exception_10() {
        final RestRequest req = () -> "http://nowhere";
        final var response = new MockHttpResponse<>(500);

        final var ex = Assertions.assertThrows(UnhandledResponseException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(response), null, null).get(new RestFnConfig())
                        .applyForResponse(req));

        Assertions.assertEquals(InternalServerErrorException.class, ex.getCause().getClass());
        Assertions.assertEquals(500, ex.getCause().statusCode());
    }

    @Test
    void exception_11() {
        final RestRequest req = () -> "http://nowhere";
        final var expected = new InterruptedException();

        final var ex = Assertions.assertThrows(RestFnException.class,
                () -> new DefaultRestFnProvider(new MockHttpRequestBuilder(), handlerProvider.toProvider(),
                        MockClientBuilderSupplier.supplier(expected), null, null).get(new RestFnConfig())
                        .applyForResponse(req));

        Assertions.assertEquals(true, Thread.currentThread().isInterrupted());
        Assertions.assertEquals(expected, ex.getCause());
    }
}
