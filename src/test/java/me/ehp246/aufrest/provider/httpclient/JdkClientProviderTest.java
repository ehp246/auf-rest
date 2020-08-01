package me.ehp246.aufrest.provider.httpclient;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.AuthenticationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.Request;
import me.ehp246.aufrest.mock.MockResponse;

/**
 * @author Lei Yang
 *
 */
class JdkClientProviderTest {
	private final static String POSTMAN_ECHO = "https://postman-echo.com/";
	private final static String BEARER_TOKEN = "I'm a bearer.";
	private final static String BASIC_USERNAME = "iam";
	private final static String BASIC_PASSWORD = "root";

	private final AtomicReference<HttpRequest> reqRef = new AtomicReference<>();
	private final AtomicReference<Integer> clientBuilderCallCountRef = new AtomicReference<>(0);
	private final AtomicReference<Integer> requestBuilderCallCountRef = new AtomicReference<>(0);
	private final AtomicReference<Duration> connectTimeoutRef = new AtomicReference<>();

	private final List<AtomicReference<?>> refs = List.of(reqRef, clientBuilderCallCountRef, connectTimeoutRef,
			requestBuilderCallCountRef);

	private final HttpClient client = Mockito.mock(HttpClient.class);
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
			CALLS_REAL_METHODS);

	private final AuthenticationProvider authProvider = uri -> {
		if (uri.toString().contains("bearer")) {
			return HttpUtils.bearer(BEARER_TOKEN);
		} else if (uri.toString().contains("basic")) {
			return HttpUtils.basicAuth(BASIC_USERNAME, BASIC_PASSWORD);
		}
		return null;
	};

	private final ClientConfig clientConfig = new ClientConfig() {
	};

	private final JdkClientProvider clientProvider = new JdkClientProvider(() -> {
		clientBuilderCallCountRef.getAndUpdate(i -> i == null ? 1 : ++i);
		try {
			Mockito.when(client.send(Mockito.any(), Mockito.any())).then(invocation -> {
				reqRef.set(invocation.getArgument(0));
				return new MockResponse<>();
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
	}, reqBuilderSupplier, clientConfig, authProvider);

	@BeforeEach
	void beforeAll() {
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
		final var clientProvider = new JdkClientProvider(client::builder, HttpRequest::newBuilder, clientConfig, null);
		final var count = (int) (Math.random() * 20);

		IntStream.range(0, count).forEach(i -> clientProvider.get());

		Assertions.assertEquals(count, client.builderCount(), "Should ask for a new builder for each client");
	}

	@Test
	void requst_builder_001() {
		final var client = new MockClientBuilderSupplier();

		final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
				CALLS_REAL_METHODS);

		final var clientProvider = new JdkClientProvider(client::builder, reqBuilderSupplier, clientConfig, null);
		final var httpFn = clientProvider.get();

		final int count = (int) (Math.random() * 20);

		IntStream.range(0, count).forEach(i -> httpFn.apply(() -> POSTMAN_ECHO));

		Mockito.verify(reqBuilderSupplier,
				Mockito.times(count).description("Should ask for a new builder for each request")).get();
	}

	@Test
	void general001() {
		final var url = POSTMAN_ECHO + "get?foo1=bar1&foo2=bar2";
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return url;
			}

			@Override
			public String method() {
				return "POST";
			}
		});

		final var httpReq = reqRef.get();

		Assertions.assertEquals("POST", httpReq.method());
		Assertions.assertEquals(url, httpReq.uri().toString());
	}

	@Test
	void auth001() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO;
			}

			@Override
			public String method() {
				return "POST";
			}
		});

		final var headers = reqRef.get().headers();

		Assertions.assertEquals(true, headers.firstValue(HttpUtils.AUTHORIZATION).isEmpty());
	}

	@Test
	void auth002() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO;
			}

			@Override
			public String method() {
				return "POST";
			}
		});

		Assertions.assertEquals(true, reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).isEmpty(),
				"Should tolerate null");
	}

	@Test
	void auth003() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/bearer";
			}

			@Override
			public String method() {
				return "POST";
			}
		});

		Assertions.assertEquals(HttpUtils.bearer(BEARER_TOKEN),
				reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get());
	}

	@Test
	void basicAuth001() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/basic";
			}

			@Override
			public String method() {
				return "POST";
			}
		});

		Assertions.assertEquals(HttpUtils.basicAuth(BASIC_USERNAME, BASIC_PASSWORD),
				reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get());
	}

	@Test
	void timeout_global_default_001() {
		final var client = new MockClientBuilderSupplier();

		final var clientProvider = new JdkClientProvider(client::builder, HttpRequest::newBuilder, clientConfig, null);

		clientProvider.get().apply(() -> "http://tonowhere");

		Assertions.assertEquals(true, client.request().timeout().isEmpty(), "Should have no timeout on request");
	}

	@Test
	void timeout_per_client_001() {
		final var client = new MockClientBuilderSupplier();

		final var clientProvider = new JdkClientProvider(client::builder, HttpRequest::newBuilder, new ClientConfig() {

			@Override
			public Duration requestTimeout() {
				return Duration.ofDays(2);
			}

		}, null);

		clientProvider.get().apply(() -> "http://tonowhere");

		Assertions.assertEquals(2, client.request().timeout().get().toDays(), "Should have take timeout on the client");
	}

	@Test
	void timeout_per_request_001() {
		final var client = new MockClientBuilderSupplier();

		final var clientProvider = new JdkClientProvider(client::builder, HttpRequest::newBuilder, new ClientConfig() {

			@Override
			public Duration requestTimeout() {
				return Duration.ofDays(2);
			}

		}, null);

		final var httpFn = clientProvider.get();

		httpFn.apply(new Request() {

			@Override
			public Duration timeout() {
				return Duration.ofHours(1);
			}

			@Override
			public String uri() {
				return "http://tonowhere";
			}
		});

		Assertions.assertEquals(60, client.request().timeout().get().toMinutes(),
				"Should have take timeout on the request");

		httpFn.apply(new Request() {

			@Override
			public Duration timeout() {
				return Duration.ofMillis(3);
			}

			@Override
			public String uri() {
				return "http://tonowhere";
			}
		});

		Assertions.assertEquals(3, client.request().timeout().get().toMillis(),
				"Should have take timeout on the request");
	}

	@Test
	void uri001() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		});

		Assertions.assertEquals("http://nowhere", reqRef.get().uri().toString());
	}
}
