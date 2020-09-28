package me.ehp246.aufrest.integration.postman;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import me.ehp246.aufrest.api.rest.HeaderContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { PostmanApp.class }, properties = { "echo.base = https://postman-echo.com",
		"me.ehp246.aufrest.connectTimeout=50000", "me.ehp246.aufrest.responseTimeout=50000" })
@ActiveProfiles("headerProvider")
class PostmanHeaderProviderTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@BeforeEach
	void clear() {
		HeaderContext.clear();
	}

	@Test
	void header_provider_001() {
		final var response = factory.getBean(HeaderTestCase001.class).get();

		Assertions.assertEquals("1, 2", response.getHeaders().get(PostmanApp.HEADERS.get(0)));
		Assertions.assertEquals("3", response.getHeaders().get(PostmanApp.HEADERS.get(1)));
	}

	@Test
	void headers_001() {
		final var value = UUID.randomUUID().toString();
		final var response = factory.getBean(HeaderTestCase001.class).get(value);

		Assertions.assertEquals(value, response.getHeaders().get("x-aufrest-trace-id"));
		Assertions.assertEquals("3", response.getHeaders().get(PostmanApp.HEADERS.get(1)));
	}

	@Test
	void headers_002() {
		final var value = UUID.randomUUID().toString();

		HeaderContext.add("x-aufrest-trace-id", value);

		final var response = factory.getBean(HeaderTestCase001.class).get();

		Assertions.assertEquals(value, response.getHeaders().get("x-aufrest-trace-id"));
		Assertions.assertEquals("3", response.getHeaders().get(PostmanApp.HEADERS.get(1)));
	}

	@Test
	void headers_003() {
		HeaderContext.add("x-aufrest-trace-id", UUID.randomUUID().toString());

		final var value = UUID.randomUUID().toString();

		final var response = factory.getBean(HeaderTestCase001.class).get(value);

		Assertions.assertEquals(value, response.getHeaders().get("x-aufrest-trace-id"));
		Assertions.assertEquals("3", response.getHeaders().get(PostmanApp.HEADERS.get(1)));
	}
}
