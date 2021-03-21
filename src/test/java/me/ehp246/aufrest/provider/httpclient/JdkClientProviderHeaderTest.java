package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.mock.MockClientConfig;
import me.ehp246.aufrest.mock.MockReq;
import me.ehp246.aufrest.mock.MockHttpResponse;

/**
 * @author Lei Yang
 *
 */
class JdkClientProviderHeaderTest {
	private final AtomicReference<HttpRequest> reqRef = new AtomicReference<>();

	private final HttpClient client = Mockito.mock(HttpClient.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier = () -> {
		try {
			Mockito.when(client.send(Mockito.any(), Mockito.any())).then(invocation -> {
				reqRef.set(invocation.getArgument(0));
				return new MockHttpResponse<>();
			});
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException();
		}
		final var builder = Mockito.mock(HttpClient.Builder.class);
		Mockito.when(builder.build()).thenReturn(client);
		Mockito.when(builder.connectTimeout(Mockito.any())).then(invocation -> builder);
		return builder;
	};

	private final ClientConfig clientConfig = new ClientConfig() {
	};

	@BeforeEach
	void beforeEach() {
		reqRef.set(null);
		HeaderContext.clear();
	}

	@Test
	void headers_001() {
		final var name = UUID.randomUUID().toString();

		HeaderContext.set(name, UUID.randomUUID().toString());

		final var req = new MockReq() {

			@Override
			public Map<String, List<String>> headers() {
				return Map.of(name, List.of(super.reqId));
			}

		};

		new JdkClientProvider(clientBuilderSupplier).get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return r -> Map.of(name, List.of(UUID.randomUUID().toString()));
			}

		}).apply(req);

		final var headers = reqRef.get().headers().map().get(name);

		Assertions.assertEquals(1, headers.size());
		Assertions.assertEquals(req.reqId, headers.get(0), "should be overwritten by Request");
	}

	@Test
	void headers_002() {
		final var name = UUID.randomUUID().toString();

		HeaderContext.set(name, UUID.randomUUID().toString());

		final var req = new MockReq() {

			@Override
			public Map<String, List<String>> headers() {
				return Map.of(reqId, List.of(super.reqId));
			}

		};

		new JdkClientProvider(clientBuilderSupplier).get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return r -> Map.of(name, List.of(UUID.randomUUID().toString()));
			}

		}).apply(req);

		final var headers = reqRef.get().headers().map();

		Assertions.assertEquals(req.reqId, headers.get(req.reqId).get(0), "Request should be merged");
		Assertions.assertEquals(1, headers.get(name).size(), "Provider should overwrite Context");
		Assertions.assertEquals(HeaderContext.values(name).get(0), headers.get(name).get(0),
				"Provider should overwrite Context");
	}

	@Test
	void header_provider_001() {
		final var value = UUID.randomUUID().toString();

		final var clientFn = new JdkClientProvider(clientBuilderSupplier).get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return r -> Map.of("header-provider", List.of(value));
			}

		});

		clientFn.apply(new MockReq());

		final var headers = reqRef.get().headers().map();

		Assertions.assertEquals(value, headers.get("header-provider").get(0));
	}

	@Test
	void header_provider_002() {
		final var name = UUID.randomUUID().toString();

		final var clientFn = new JdkClientProvider(clientBuilderSupplier).get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return r -> Map.of(name, List.of(UUID.randomUUID().toString()));
			}

		});

		final var req = new MockReq() {

			@Override
			public Map<String, List<String>> headers() {
				return Map.of(name, List.of(super.reqId));
			}

		};

		clientFn.apply(req);

		final var headers = reqRef.get().headers().map().get(name);

		Assertions.assertEquals(1, headers.size());
		Assertions.assertEquals(req.reqId, headers.get(0), "should be overwritten by Request");
	}

	@Test
	void header_provider_003() {
		final var name = UUID.randomUUID().toString();
		final var value = UUID.randomUUID().toString();

		final var clientFn = new JdkClientProvider(clientBuilderSupplier).get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return r -> Map.of(name, List.of(UUID.randomUUID().toString()));
			}

		});

		HeaderContext.set(name, value);

		clientFn.apply(new MockReq());

		final var headers = reqRef.get().headers().map().get(name);

		Assertions.assertEquals(1, headers.size());
		Assertions.assertEquals(value, headers.get(0), "should be overwritten by Context");
	}

	@Test
	void header_provider_004() {
		final var mockReq = new MockReq();
		final var reqRef = new AtomicReference<RestRequest>();

		new JdkClientProvider().get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return r -> {
					reqRef.set(r);
					return null;
				};
			}

		}).apply(mockReq);

		Assertions.assertEquals(true, reqRef.get() == mockReq, "Should be passed to the header provider");
	}

	@Test
	void header_priority_001() {
		final var name = "Filter-Them";

		HeaderContext.add(name.toLowerCase(), "2");

		new JdkClientProvider(clientBuilderSupplier).get(new ClientConfig() {

			@Override
			public HeaderProvider headerProvider() {
				return req -> Map.of(name.toLowerCase(), List.of("3"));
			}

		}).apply(new MockReq() {

			@Override
			public Map<String, List<String>> headers() {
				return Map.of("filter-Them", List.of("1"));
			}

		});

		Assertions.assertEquals("1", reqRef.get().headers().allValues(name).get(0));
	}

	@Test
	void header_priority_002() {
		final var name = "Filter-Them";

		HeaderContext.add(name, "2");

		new JdkClientProvider(clientBuilderSupplier)
				.get(new MockClientConfig(req -> Map.of(name.toLowerCase(), List.of("3")))).apply(new MockReq());

		Assertions.assertEquals("2", reqRef.get().headers().allValues(name).get(0));
	}

	@Test
	void header_priority_003() {
		final var name = "Filter-These";

		HeaderContext.add(name, "2");

		new JdkClientProvider(clientBuilderSupplier)
				.get(new MockClientConfig(req -> Map.of("merge-these", List.of("1", "2")))).apply(new MockReq());

		Assertions.assertEquals("2", reqRef.get().headers().allValues(name).get(0));
		Assertions.assertEquals(2, reqRef.get().headers().allValues("merge-these").size());
	}
}
