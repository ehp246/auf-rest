package org.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.ehp246.aufrest.api.rest.AuthenticationProvider;
import org.ehp246.aufrest.api.rest.BasicAuth;
import org.ehp246.aufrest.api.rest.BearerToken;
import org.ehp246.aufrest.api.rest.HttpFnConfig;
import org.ehp246.aufrest.api.rest.HttpUtil;
import org.ehp246.aufrest.api.rest.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
	private final AtomicReference<URI> reqUriRef = new AtomicReference<>();
	private final AtomicReference<URI> authUriRef = new AtomicReference<>();
	private final AtomicReference<Duration> connectTimeoutRef = new AtomicReference<>();
	private final AtomicReference<InvocationOnMock> mockInvocationRef = new AtomicReference<>();
	private final List<AtomicReference<?>> refs = List.of(reqUriRef);

	private final HttpClient client = Mockito.mock(HttpClient.class);

	private final AuthenticationProvider authProvider = uri -> {
		authUriRef.set(uri);
		if (uri.toString().contains("bearer")) {
			return (BearerToken) () -> BEARER_TOKEN;
		} else if (uri.toString().contains("basic")) {
			return new BasicAuth() {

				@Override
				public String username() {
					return BASIC_USERNAME;
				}

				@Override
				public String password() {
					return BASIC_PASSWORD;
				}
			};
		}
		return null;
	};

	private final JdkClientProvider clientProvider = new JdkClientProvider(() -> {
		try {
			Mockito.when(client.send(Mockito.any(), Mockito.any())).then(new Answer<HttpResponse<?>>() {
				@Override
				public HttpResponse<?> answer(final InvocationOnMock invocation) {
					reqRef.set(invocation.getArgument(0));
					return Mockito.mock(HttpResponse.class);
				}
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
	}, HttpRequest::newBuilder);

	@BeforeEach
	void beforeAll() {
		reqRef.set(null);
		connectTimeoutRef.set(null);
		authUriRef.set(null);
		mockInvocationRef.set(null);
		refs.stream().forEach(ref -> ref.set(null));
	}

	@Test
	void general001() {
		final var url = POSTMAN_ECHO + "get?foo1=bar1&foo2=bar2";
		clientProvider.get(new HttpFnConfig() {
		}).apply(new Request() {

			@Override
			public String uri() {
				return url;
			}

			@Override
			public String method() {
				return "POST";
			}
		}).get();

		final var httpReq = reqRef.get();

		Assertions.assertEquals("POST", httpReq.method());
		Assertions.assertEquals(url, httpReq.uri().toString());
	}

	@Test
	void auth001() {
		clientProvider.get(new HttpFnConfig() {
		}).apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO;
			}

			@Override
			public String method() {
				return "POST";
			}
		}).get();

		final var headers = reqRef.get().headers();

		Assertions.assertEquals(true, headers.firstValue(HttpUtil.AUTHORIZATION).isEmpty());
	}

	@Test
	void auth002() {
		clientProvider.get(new HttpFnConfig() {

			@Override
			public AuthenticationProvider authProvider() {
				return authProvider;
			}

		}).apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO;
			}

			@Override
			public String method() {
				return "POST";
			}
		}).get();

		Assertions.assertEquals(true, reqRef.get().headers().firstValue(HttpUtil.AUTHORIZATION).isEmpty(),
				"Should tolerate null");
	}

	@Test
	void auth003() {
		clientProvider.get(new HttpFnConfig() {

			@Override
			public AuthenticationProvider authProvider() {
				return authProvider;
			}

		}).apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/bearer";
			}

			@Override
			public String method() {
				return "POST";
			}
		}).get();

		Assertions.assertEquals(HttpUtil.bearer(BEARER_TOKEN),
				reqRef.get().headers().firstValue(HttpUtil.AUTHORIZATION).get());
	}

	@Test
	void basicAuth001() {
		clientProvider.get(new HttpFnConfig() {

			@Override
			public AuthenticationProvider authProvider() {
				return authProvider;
			}

		}).apply(new Request() {

			@Override
			public String uri() {
				return POSTMAN_ECHO + "/basic";
			}

			@Override
			public String method() {
				return "POST";
			}
		}).get();

		Assertions.assertEquals(HttpUtil.basicAuth(BASIC_USERNAME, BASIC_PASSWORD),
				reqRef.get().headers().firstValue(HttpUtil.AUTHORIZATION).get());
	}

	@Test
	void timeout001() {
		clientProvider.get(new HttpFnConfig() {

			@Override
			public Duration connectTimeout() {
				return Duration.ofMillis(1);
			}

			@Override
			public Duration responseTimeout() {
				return Duration.ofMillis(2);
			}
		}).apply(new Request() {

			@Override
			public String uri() {
				return "http://tonowhere.com";
			}
		}).get();

		Assertions.assertEquals(2, reqRef.get().timeout().get().toMillis());
	}

	@Test
	void uri001() {
		clientProvider.get(new HttpFnConfig() {
		}).apply(new Request() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		}).get();

		Assertions.assertEquals("http://nowhere", reqRef.get().uri().toString());
	}
}
