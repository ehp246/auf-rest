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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.ByRestListener;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
class DefaultRestFnProviderTest {
	private final AtomicReference<Integer> clientBuilderCallCountRef = new AtomicReference<>(0);
	private final AtomicReference<Duration> connectTimeoutRef = new AtomicReference<>();

	private final List<AtomicReference<?>> refs = List.of(clientBuilderCallCountRef, connectTimeoutRef);

	private final DefaultRestFnProvider clientProvider = new DefaultRestFnProvider(() -> {
		clientBuilderCallCountRef.getAndUpdate(i -> i == null ? 1 : ++i);
		final HttpClient client = Mockito.mock(HttpClient.class);

		try {
			Mockito.when(client.send(Mockito.any(), Mockito.any())).then(invocation -> {
				return new MockHttpResponse<>();
			});
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException();
		}
		final var builder = Mockito.mock(HttpClient.Builder.class);
		Mockito.when(builder.build()).thenReturn(client);
		Mockito.when(builder.connectTimeout(Mockito.any())).then(invocation -> {
			connectTimeoutRef.set(invocation.getArgument(0));
			return builder;
		});
		return builder;
	});

	@BeforeEach
	void beforeEach() {
		refs.stream().forEach(ref -> ref.set(null));
	}

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

		IntStream.range(0, count).forEach(i -> clientProvider.get(new ClientConfig() {
		}));

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

		new DefaultRestFnProvider(() -> mockBuilder).get(new ClientConfig() {
		});

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

		new DefaultRestFnProvider(() -> mockBuilder).get(new ClientConfig() {

			@Override
			public Duration connectTimeout() {
				return timeout;
			}

		});

		Assertions.assertEquals(timeout, ref.get());
	}

	@Test
	void consumer_001() {
		final var mockedReq = Mockito.mock(HttpRequest.class);
		final RequestBuilder reqBuilder = req -> mockedReq;

		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};

		final var map = new HashMap<>();
		final var orig = new RuntimeException("This is a test");
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		final var obs = List.of(new ByRestListener() {

			@Override
			public void onRequest(HttpRequest httpRequest, RestRequest req) {
				map.put("1", httpRequest);
				map.put("2", req);
			}

		}, new ByRestListener() {

			@Override
			public void onRequest(HttpRequest httpRequest, RestRequest req) {
				map.put("3", httpRequest);
				map.put("4", req);
			}

			@Override
			public void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
				map.put("5", exception);
			}
		});

		new DefaultRestFnProvider(clientBuilderSupplier::builder, reqBuilder, obs).get(new ClientConfig() {
		}).apply(req);

		Exception ex = null;
		try {
			new DefaultRestFnProvider(new MockClientBuilderSupplier(orig)::builder, reqBuilder, obs)
					.get(new ClientConfig() {
					}).apply(req);
		} catch (Exception e) {
			ex = e;
		}

		Assertions.assertEquals(true, map.get("1") == mockedReq);
		Assertions.assertEquals(true, map.get("1") == map.get("3"));
		Assertions.assertEquals(true, map.get("2") == map.get("4"));

		Assertions.assertEquals(true, map.get("5") == orig);
		Assertions.assertEquals(true, ex.getCause() == orig);
	}
}
