package me.ehp246.aufrest.integration.postman;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.ClientConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { PostmanApp.class }, properties = { "echo.base = https://postman-echo.com",
		"me.ehp246.aufrest.connectTimeout=50000", "me.ehp246.aufrest.responseTimeout=50000" })
class EnableByRestPostmanTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@Test
	void clientConfig_001() {
		final var bean = factory.getBean(ClientConfig.class);

		Assertions.assertEquals(50000, bean.connectTimeout().toMillis());
		Assertions.assertEquals(50000, bean.responseTimeout().toMillis());
	}

	@Test
	@Disabled
	void get_001() {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var response = newInstance.getAsHttpResponse();

		Assertions.assertEquals(true, response.body() instanceof String);
		Assertions.assertEquals(true, response.body().toString().length() > 10);
	}

	@Test
	void get_002() {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var response = newInstance.getAsInputStream();

		Assertions.assertEquals(true, response instanceof InputStream);
	}

	@Test
	void get_003() {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		newInstance.getVoid();

		final var response = newInstance.getVoid2();

		Assertions.assertEquals(true, response == null);
	}

	@Test
	@Disabled
	void get_004() throws InterruptedException, ExecutionException {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var responseFuture = newInstance.getAsResponseFuture();

		Assertions.assertEquals(true, responseFuture instanceof CompletableFuture);
		Assertions.assertEquals(true, responseFuture.get().body() instanceof String,
				"Should default to ResponseSupplier with String body");
	}

	@Test
	void get_005() throws InterruptedException, ExecutionException {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var response = newInstance.getAsEchoBody();

		Assertions.assertEquals(true, response instanceof EchoResponseBody);
		Assertions.assertEquals("https://postman-echo.com/get", response.getUrl());
	}

	@Test
	void post_001() {
		final var newInstance = factory.getBean(EchoPostTestCase001.class);

		final var response = newInstance.post("1234");

		Assertions.assertEquals("\"1234\"", response.getData());
	}

	@Test
	void post_002() {
		final var newInstance = factory.getBean(EchoPostTestCase001.class);

		final var response = newInstance.post(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void put_001() {
		final var newInstance = factory.getBean(EchoPutTestCase001.class);

		final var response = newInstance.put(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void patch001() {
		final var newInstance = factory.getBean(EchoPatchTestCase001.class);

		final var response = newInstance.patch(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void delete001() {
		final var newInstance = factory.getBean(EchoDeleteTestCase001.class);

		final var response = newInstance.delete(Map.of("firstName", "Rest", "lastName", "Auf"));

		Assertions.assertEquals("Rest", response.getJson().get("firstName"));
		Assertions.assertEquals("Auf", response.getJson().get("lastName"));
	}

	@Test
	void basci_auth_001() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase001.class);

		Assertions.assertEquals(true, newInstance.get().get("authenticated"));

		Assertions.assertThrows(UnhandledResponseException.class, newInstance::get,
				"Should throw on the second call because the global authentication provider allows only one call");

		/*
		 * If the return type is HttpResponse, the invocation should not throw as long
		 * as a response is received and can be returned.
		 */
		final var response = Assertions.assertDoesNotThrow(newInstance::getAsResponse,
				"Should return a valid response instead of throwing");

		Assertions.assertEquals(401, response.statusCode(), "Should have correct status code");
		Assertions.assertEquals("Unauthorized", response.body(), "Should return response body as string");
	}

	@Test
	void basci_auth_002() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase002.class);

		Assertions.assertEquals(true, newInstance.get().get("authenticated"));

		Assertions.assertEquals(true, newInstance.get().get("authenticated"),
				"Should work on the second call because the global autentication provider is by-passed");
	}

	@Test
	void basci_auth_003() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase003.class);

		Assertions.assertThrows(UnhandledResponseException.class, newInstance::get,
				"Should not work because of the wrong authentication type");
	}

	@Test
	void basci_auth_004() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase004.class);

		Assertions.assertEquals(true, newInstance.get().get("authenticated"));
	}

	@Test
	void timeout001() {
		Assertions.assertThrows(RuntimeException.class, factory.getBean(EchoTimeoutTestCase.class)::get,
				"Can you complete it in one millisecond?");
	}
}
