package org.ehp246.aufrest.integration;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.ehp246.aufrest.api.rest.AuthenticationProvider;
import org.ehp246.aufrest.api.rest.BasicAuth;
import org.ehp246.aufrest.api.rest.HttpFnConfig;
import org.ehp246.aufrest.api.rest.Request;
import org.ehp246.aufrest.api.rest.Response;
import org.ehp246.aufrest.core.byrest.ByRestFactory;
import org.ehp246.aufrest.provider.httpclient.JdkClientProvider;
import org.ehp246.aufrest.provider.jackson.JsonByJackson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * @author Lei Yang
 *
 */
public class ByRestFactoryEchoTest {
	private final JdkClientProvider client = new JdkClientProvider(HttpClient::newBuilder, HttpRequest::newBuilder);

	private final MockEnvironment env = new MockEnvironment().withProperty("echo.base", "https://postman-echo.com");
	private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule());
	private final JsonByJackson bodyBuilder = new JsonByJackson(objectMapper);
	private final ByRestFactory factory = new ByRestFactory(client, env, bodyBuilder.getFromText(),
			bodyBuilder.getToText(), new HttpFnConfig() {
				private int count = 0;

				@Override
				public AuthenticationProvider authProvider() {
					return uri -> {
						// Only allow one call.
						if (uri.getPath().contains("basic-auth") && count == 0) {
							count++;
							return new BasicAuth() {

								@Override
								public String username() {
									return "postman";
								}

								@Override
								public String password() {
									return "password";
								}
							};
						}
						return null;
					};
				}

			});

	@Test
	void returnType001() {
		final var newInstance = factory.newInstance(EchoGetTestCase001.class);

		final var response = newInstance.getAsResponse();

		Assertions.assertEquals(true, response instanceof Response);
		Assertions.assertEquals(true, response.request() instanceof Request);
		Assertions.assertEquals(true, response.received().body() instanceof String);
		Assertions.assertEquals(true, response.received().body().toString().length() > 10);
		Assertions.assertEquals(true, response.received().headers().map().size() > 1);
		Assertions.assertEquals(true, response.received() instanceof HttpResponse);
	}

	@Test
	void returnType002() {
		final var newInstance = factory.newInstance(EchoGetTestCase001.class);

		final var response = newInstance.getAsHttpResponse();

		Assertions.assertEquals(true, response.body() instanceof String);
		Assertions.assertEquals(true, response.body().toString().length() > 10);
	}

	@Test
	void returnType003() {
		final var newInstance = factory.newInstance(EchoGetTestCase001.class);

		final var response = newInstance.getAsInputStream();

		Assertions.assertEquals(true, response instanceof InputStream);
	}

	@Test
	void returnType004() {
		final var newInstance = factory.newInstance(EchoGetTestCase001.class);

		newInstance.getVoid();

		final var response = newInstance.getVoid2();

		Assertions.assertEquals(true, response == null);
	}

	@Test
	void returnType005() throws InterruptedException, ExecutionException {
		final var newInstance = factory.newInstance(EchoGetTestCase001.class);

		final var responseFuture = newInstance.getAsResponseFuture();

		Assertions.assertEquals(true, responseFuture instanceof CompletableFuture);
		Assertions.assertEquals(true, responseFuture.get().received().body() instanceof String,
				"Should default to Response with String body");
	}

	@Test
	void returnType006() throws InterruptedException, ExecutionException {
		final var newInstance = factory.newInstance(EchoGetTestCase001.class);

		final var response = newInstance.getAsEchoBody();

		Assertions.assertEquals(true, response instanceof EchoResponseBody);
		Assertions.assertEquals("https://postman-echo.com/get", response.getUrl());
	}

	@Test
	void post001() {
		final var newInstance = factory.newInstance(EchoPostTestCase001.class);

		final var response = newInstance.post("1234");

		Assertions.assertEquals("\"1234\"", response.getData());
	}

	@Test
	void post002() {
		final var newInstance = factory.newInstance(EchoPostTestCase001.class);

		final var response = newInstance.post(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void put001() {
		final var newInstance = factory.newInstance(EchoPutTestCase001.class);

		final var response = newInstance.put(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void patch001() {
		final var newInstance = factory.newInstance(EchoPatchTestCase001.class);

		final var response = newInstance.patch(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void delete001() {
		final var newInstance = factory.newInstance(EchoDeleteTestCase001.class);

		final var response = newInstance.delete(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void basciAuth001() {
		final var newInstance = factory.newInstance(EchoAuthTestCase001.class);

		final var map = newInstance.get();

		Assertions.assertEquals(true, map.get("authenticated"));

		Assertions.assertThrows(RuntimeException.class, newInstance::get,
				"Should throw on the second call because the authenticator allows only one call");
	}
}
