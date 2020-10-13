package me.ehp246.aufrest.provider.httpclient;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.Request;
import me.ehp246.aufrest.mock.MockReq;
import me.ehp246.aufrest.mock.MockResponse;

/**
 * @author Lei Yang
 *
 */
class JdkClientProviderTest {
	private final static String POSTMAN_ECHO = "https://postman-echo.com";
	private final static String BEARER = "I'm a bearer.";
	private final static String BASIC = "I'm basic.";

	private final AtomicReference<HttpRequest> reqRef = new AtomicReference<>();
	private final AtomicReference<Integer> clientBuilderCallCountRef = new AtomicReference<>(0);
	private final AtomicReference<Integer> requestBuilderCallCountRef = new AtomicReference<>(0);
	private final AtomicReference<Duration> connectTimeoutRef = new AtomicReference<>();

	private final List<AtomicReference<?>> refs = List.of(reqRef, clientBuilderCallCountRef, connectTimeoutRef,
			requestBuilderCallCountRef);

	private final HttpClient client = Mockito.mock(HttpClient.class);

	private final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
			CALLS_REAL_METHODS);

	private final AuthorizationProvider authProvider = new AuthorizationProvider() {
		private int count = 0;

		@Override
		public String get(final String uri) {
			if (uri.toString().contains("bearer")) {
				return BEARER;
			} else if (uri.toString().contains("basic")) {
				return BASIC;
			} else if (uri.toString().contains("count")) {
				return ++count + "";
			}
			return null;
		}
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
	}, reqBuilderSupplier, clientConfig, authProvider, null);

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
		final var clientProvider = new JdkClientProvider(client::builder, HttpRequest::newBuilder, clientConfig);
		final var count = (int) (Math.random() * 20);

		IntStream.range(0, count).forEach(i -> clientProvider.get());

		Assertions.assertEquals(count, client.builderCount(), "Should ask for a new builder for each client");
	}

	@Test
	void requst_builder_001() {
		final var client = new MockClientBuilderSupplier();

		final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
				CALLS_REAL_METHODS);

		final var clientProvider = new JdkClientProvider(client::builder, reqBuilderSupplier, clientConfig);
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
	void auth_null_001() {
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
	void auth_req_002() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO;
			}

			@Override
			public String method() {
				return "POST";
			}

			@Override
			public Supplier<String> authSupplier() {
				return () -> "req auth header";
			}

		});

		Assertions.assertEquals("req auth header", reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get(),
				"Should be from request");
	}

	@Test
	void auth_req_004() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/bearer";
			}

			@Override
			public String method() {
				return "POST";
			}

			@Override
			public Supplier<String> authSupplier() {
				return () -> null;
			}
		});

		Assertions.assertEquals(true, reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).isEmpty(),
				"should be from request");
	}

	@Test
	void auth_req_005() {
		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/bearer";
			}

			@Override
			public String method() {
				return "POST";
			}

			@Override
			public Supplier<String> authSupplier() {
				return () -> "   ";
			}
		});

		Assertions.assertEquals(true, reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).isEmpty());
	}

	@Test
	void auth_global_001() {
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

		Assertions.assertEquals(BEARER, reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get());
	}

	@Test
	void auth_global_002() {
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

		Assertions.assertEquals(BASIC, reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get());
	}

	@Test
	void auth_global_003() {
		final var request = new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/count";
			}

			@Override
			public String method() {
				return "POST";
			}
		};

		clientProvider.get().apply(request);

		Assertions.assertEquals(1, reqRef.get().headers().allValues("authorization").size());

		Assertions.assertEquals("1", reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get());

		clientProvider.get().apply(request);

		Assertions.assertEquals("2", reqRef.get().headers().firstValue(HttpUtils.AUTHORIZATION).get(),
				"should be dynamic on invocations");
	}

	@Test
	void auth_header_001() {
		HeaderContext.add("authorization", UUID.randomUUID().toString());

		final var request = new MockReq() {
			@Override
			public Map<String, List<String>> headers() {
				return Map.of("authorization", List.of(UUID.randomUUID().toString()));
			}

			@Override
			public Supplier<String> authSupplier() {
				return () -> null;
			}

		};

		clientProvider.get().apply(request);

		Assertions.assertEquals(0, reqRef.get().headers().allValues("authorization").size(),
				"Request has Authorization explicitly off");
	}

	@Test
	void timeout_global_connect_001() {
		final HttpClient.Builder mockBuilder = Mockito.mock(HttpClient.Builder.class);
		final var ref = new AtomicReference<Duration>();

		Mockito.when(mockBuilder.connectTimeout(Mockito.any())).thenAnswer(invocation -> {
			ref.set(invocation.getArgument(0));
			return mockBuilder;
		});

		new JdkClientProvider(() -> mockBuilder, HttpRequest::newBuilder, new ClientConfig() {
		}).get();

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

		new JdkClientProvider(() -> mockBuilder, HttpRequest::newBuilder, new ClientConfig() {

			@Override
			public Duration connectTimeout() {
				return timeout;
			}

		}).get();

		Assertions.assertEquals(timeout, ref.get());
	}

	@Test
	void timeout_global_reponse_001() {
		final var client = new MockClientBuilderSupplier();

		new JdkClientProvider(client::builder, HttpRequest::newBuilder, new ClientConfig() {
		}).get().apply(() -> "http://tonowhere");

		Assertions.assertEquals(true, client.request().timeout().isEmpty(), "Should have no timeout on request");
	}

	@Test
	void timeout_global_reponse_002() {
		final var client = new MockClientBuilderSupplier();

		new JdkClientProvider(client::builder, HttpRequest::newBuilder, new ClientConfig() {

			@Override
			public Duration responseTimeout() {
				return Duration.ofDays(1);
			}

		}).get().apply(() -> "http://tonowhere");

		Assertions.assertEquals(1, client.request().timeout().get().toDays());
	}

	@Test
	void timeout_per_request_001() {
		final var client = new MockClientBuilderSupplier();

		final var clientProvider = new JdkClientProvider(client::builder, HttpRequest::newBuilder, new ClientConfig() {

			@Override
			public Duration responseTimeout() {
				return Duration.ofDays(2);
			}

		});

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

	@Test
	void requst_header_001() {
		clientProvider.get().apply(new MockReq() {

			@Override
			public Map<String, List<String>> headers() {
				return Map.of("accept-language", List.of("CN", "EN", ""), "x-correl-id", List.of("uuid"));
			}

		});

		final var map = reqRef.get().headers().map();

		Assertions.assertEquals(2, map.get("accept-language").size(), "should filter out all blank values");
		Assertions.assertEquals(1, map.get("x-correl-id").size());
	}

	@Test
	void header_context_001() {
		HeaderContext.set("accept-language", "DE");

		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return "http://nowhere";
			}

			@Override
			public Map<String, List<String>> headers() {
				return null;
			}

		});

		final var map = reqRef.get().headers().map();

		final var accept = map.get("accept-language");

		Assertions.assertEquals(1, accept.size(), "should have context headers");
		Assertions.assertEquals("DE", accept.get(0));
	}

	@Test
	void header_context_002() {
		HeaderContext.set("accept-language", "DE");

		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return "http://nowhere";
			}

			@Override
			public Map<String, List<String>> headers() {
				return Map.of("x-correl-id", List.of("uuid"));
			}

		});

		final var map = reqRef.get().headers().map();

		final var accept = map.get("accept-language");

		Assertions.assertEquals(1, accept.size(), "should have context headers");
		Assertions.assertEquals("DE", accept.get(0));
		Assertions.assertEquals(1, map.get("x-correl-id").size(), "should merge");
	}

	@Test
	void header_context_003() {
		HeaderContext.set("accept-language", "DE");

		clientProvider.get().apply(new Request() {

			@Override
			public String uri() {
				return "http://nowhere";
			}

			@Override
			public Map<String, List<String>> headers() {
				return Map.of("accept-language", List.of("EN"));
			}

		});

		final var map = reqRef.get().headers().map();

		final var accept = map.get("accept-language");

		Assertions.assertEquals(1, accept.size(), "should override context headers");
		Assertions.assertEquals("EN", accept.get(0));
	}

	@Test
	void conneg_test001() {
		clientProvider.get().apply(() -> "http://nowhere");

		final var contentType = reqRef.get().headers().map().get("content-type");

		Assertions.assertEquals(1, contentType.size());
		Assertions.assertEquals("application/json", contentType.get(0));

		final var accept = reqRef.get().headers().map().get("accept");

		Assertions.assertEquals(1, accept.size());
		Assertions.assertEquals("application/json", accept.get(0));
	}

	@Test
	void conneg_test002() {
		clientProvider.get().apply(new Request() {
			@Override
			public String uri() {
				return "http://nowhere";
			}

			@Override
			public String contentType() {
				return "produce this";
			}

			@Override
			public String accept() {
				return "consume that";
			}

		});

		final var contentType = reqRef.get().headers().map().get("content-type");

		Assertions.assertEquals(1, contentType.size());
		Assertions.assertEquals("produce this", contentType.get(0));

		final var accept = reqRef.get().headers().map().get("accept");

		Assertions.assertEquals(1, accept.size());
		Assertions.assertEquals("consume that", accept.get(0));
	}
}
