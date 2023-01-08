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

import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;
import me.ehp246.test.mock.MockHttpRequestBuilder;

/**
 * @author Lei Yang
 *
 */
class DefaultRestFnProviderTest {
    /**
     * For each get call on the client provider, the provider should ask
     * client-builder supplier for a new builder. It should not re-use
     * previous-acquired builder.
     */
    @Test
    void client_builder_001() {
        final var client = new MockClientBuilderSupplier();
        final var clientProvider = new DefaultRestFnProvider(client::builder);
        final var count = (int) (Math.random() * 20);

        IntStream.range(0, count).forEach(i -> clientProvider.get(new ClientConfig()));

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

        new DefaultRestFnProvider(() -> mockBuilder).get(new ClientConfig());

        Assertions.assertEquals(null, ref.get());
    }

    @Test
    void timeout_global_connect_002() {
        final HttpClient.Builder mockBuilder = Mockito.mock(HttpClient.Builder.class);
        final var ref = new AtomicReference<Duration>();
        final var timeout = Duration.ofDays(1);

        Mockito.when(mockBuilder.connectTimeout(Mockito.any())).thenAnswer(invocation -> {
            ref.set(invocation.getArgument(0));
            return mockBuilder;
        });

        new DefaultRestFnProvider(() -> mockBuilder).get(new ClientConfig(timeout));

        Assertions.assertEquals(timeout, ref.get());
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

        new DefaultRestFnProvider(clientBuilderSupplier::builder, reqBuilder, obs).get(new ClientConfig()).applyForResponse(req);

        Assertions.assertEquals(true, map.get("1") == mockedReq);
        Assertions.assertEquals(true, map.get("1") == map.get("3"));
        Assertions.assertEquals(true, map.get("2") == map.get("4"));
    }

    @Test
    void listener_002() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";

        final var map = new HashMap<>();
        final var orig = new RuntimeException("This is a test");

        Exception ex = null;
        try {
            new DefaultRestFnProvider(new MockClientBuilderSupplier(orig)::builder, reqBuilder,
                    List.of(new RestListener() {

                        @Override
                        public void onException(final Exception exception, final HttpRequest httpRequest,
                                final RestRequest req) {
                            map.put("5", exception);
                        }
                    })).get(new ClientConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        Assertions.assertEquals(true, map.get("5") == null, "should have no wrap");
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
            new DefaultRestFnProvider(new MockClientBuilderSupplier(orig)::builder, reqBuilder,
                    List.of(new RestListener() {

                        @Override
                        public void onException(final Exception exception, final HttpRequest httpRequest,
                                final RestRequest req) {
                            map.put("5", exception);
                        }
                    })).get(new ClientConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        Assertions.assertEquals(true, map.get("5") == orig, "should just be caught by Fn");
        Assertions.assertEquals(true, ex.getCause() == orig);
    }

    @Test
    void listener_004() {
        final var mockedReq = Mockito.mock(HttpRequest.class);
        final HttpRequestBuilder reqBuilder = new MockHttpRequestBuilder(mockedReq);

        final var req = (RestRequest) () -> "http://nowhere";
        final var orig = new IllegalArgumentException("This is a test");

        Exception ex = null;
        try {
            new DefaultRestFnProvider(new MockClientBuilderSupplier()::builder, reqBuilder, List.of(new RestListener() {

                @Override
                public void onRequest(final HttpRequest httpRequest, final RestRequest req) {
                    throw orig;
                }
            })).get(new ClientConfig()).applyForResponse(req);
        } catch (final Exception e) {
            ex = e;
        }

        Assertions.assertEquals(true, ex == orig, "should have no wrap");
    }

    @Test
    void exception_001() {
        final var toBeThrown = new IOException();

        final var ex = Assertions
                .assertThrows(RestFnException.class,
                        () -> new DefaultRestFnProvider(new MockClientBuilderSupplier(toBeThrown)::builder,
                                new MockHttpRequestBuilder(), null).get(new ClientConfig())
                                        .applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(true, ex.getCause() == toBeThrown);
    }

    @Test
    void exception_002() {
        final var toBeThrown = new InterruptedException();

        final var ex = Assertions.assertThrows(RestFnException.class,
                () -> new DefaultRestFnProvider(new MockClientBuilderSupplier(toBeThrown)::builder,
                        new MockHttpRequestBuilder(), null).get(new ClientConfig()).applyForResponse(() -> "http://nowhere"));

        Assertions.assertEquals(true, ex.getCause() == toBeThrown);
    }
}
