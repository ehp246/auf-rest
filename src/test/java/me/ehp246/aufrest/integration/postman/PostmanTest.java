package me.ehp246.aufrest.integration.postman;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { PostmanApp.class }, properties = { "echo.base = https://postman-echo.com",
		"me.ehp246.aufrest.connectTimeout=50000", "me.ehp246.aufrest.responseTimeout=50000" })
class PostmanTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@BeforeEach
	void clear() {
		HeaderContext.remove();
	}

	@Test
	void clientConfig_001() {
		final var bean = factory.getBean(ClientConfig.class);

		Assertions.assertEquals(50000, bean.connectTimeout().toMillis());
		Assertions.assertEquals(50000, bean.responseTimeout().toMillis());
	}

	@Test
	void future_001() throws InterruptedException, ExecutionException, JsonMappingException, BeansException,
			JsonProcessingException {
		final var responseFuture = factory.getBean(EchoGetTestCase001.class).getAsResponseFuture();

		Assertions.assertEquals(true, responseFuture instanceof CompletableFuture);

		final var httpResponse = responseFuture.get();

		Assertions.assertEquals(true, httpResponse instanceof HttpResponse);

		final var body = httpResponse.body();

		Assertions.assertEquals(true, body instanceof String, "Should default to String body");

		Assertions
				.assertDoesNotThrow(() -> factory.getBean(ObjectMapper.class).readValue(body, EchoResponseBody.class));
	}

	@Test
	void httpresponse_001() {
		final var body = factory.getBean(EchoGetTestCase001.class).getAsHttpResponse().body();

		Assertions.assertEquals(true, body instanceof String);
		Assertions
				.assertDoesNotThrow(() -> factory.getBean(ObjectMapper.class).readValue(body, EchoResponseBody.class));
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
	void get_005() throws InterruptedException, ExecutionException {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var response = newInstance.getAsEchoBody();

		Assertions.assertEquals(true, response instanceof EchoResponseBody);
		Assertions.assertEquals("https://postman-echo.com/get", response.getUrl());
	}

	@Test
	void get_006() {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var id = UUID.randomUUID().toString();
		final var response = newInstance.getAsEchoBody(id);

		final var headers = response.getHeaders();
		Assertions.assertEquals(id, headers.get("x-auf-rest-id"));
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
	void basic_auth_001() {
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
	void basic_auth_002() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase002.class);

		Assertions.assertEquals(true, newInstance.get().get("authenticated"));

		Assertions.assertEquals(true, newInstance.get().get("authenticated"),
				"Should work on the second call because the global autentication provider is by-passed");
	}

	@Test
	void basic_auth_003() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase003.class);

		Assertions.assertThrows(UnhandledResponseException.class, newInstance::get,
				"Should not work because of the wrong authentication type");
	}

	@Test
	void basic_auth_004() {
		final var newInstance = factory.getBean(EchoAuthTestCases.BasicCase004.class);

		Assertions.assertEquals(true, newInstance.get().get("authenticated"));
	}

	@Test
	void timeout001() {
		Assertions.assertThrows(RuntimeException.class, factory.getBean(EchoTimeoutTestCase.class)::get,
				"Can you complete it in one millisecond?");
	}

	@Test
	void header_context_001() {
		final var newInstance = factory.getBean(EchoGetTestCase001.class);

		final var name = UUID.randomUUID().toString();
		final var values = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		HeaderContext.add(name, values.get(0));

		var response = newInstance.getAsEchoBody();

		Assertions.assertEquals(values.get(0), response.getHeaders().get(name), "should have the single value");

		HeaderContext.add(name, values.get(1));

		response = newInstance.getAsEchoBody();

		Assertions.assertEquals(values.get(0) + ", " + values.get(1), response.getHeaders().get(name),
				"should have both values");

		HeaderContext.remove();

		response = newInstance.getAsEchoBody();

		Assertions.assertEquals(null, response.getHeaders().get(name), "should have no value");
	}

	@Test
	void header_context_002() {
		final var name = "x-auf-rest-id";
		final var values = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		HeaderContext.add(name, values.get(0));

		final var response = factory.getBean(EchoGetTestCase001.class).getAsEchoBody(values.get(1));

		Assertions.assertEquals(values.get(1), response.getHeaders().get(name), "should be overwritten by request");
	}
}
