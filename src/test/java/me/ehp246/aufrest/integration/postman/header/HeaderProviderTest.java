package me.ehp246.aufrest.integration.postman.header;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufrest.api.rest.HeaderContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, properties = { "echo.base = https://postman-echo.com" })
class HeaderProviderTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@BeforeEach
	void clear() {
		HeaderContext.remove();
	}

	@Test
	void header_provider_001() {
		final var response = factory.getBean(HeaderTestCase001.class).get();

		Assertions.assertEquals("1, 2", response.getHeaders().get(AppConfig.HEADERS.get(0)));
		Assertions.assertEquals("3", response.getHeaders().get(AppConfig.HEADERS.get(1)));
	}

	@Test
	void headers_001() {
		final var value = UUID.randomUUID().toString();
		final var response = factory.getBean(HeaderTestCase001.class).get(value);

		Assertions.assertEquals(value, response.getHeaders().get("x-aufrest-trace-id"));
		Assertions.assertEquals("3", response.getHeaders().get(AppConfig.HEADERS.get(1)));
	}

	@Test
	void headers_002() {
		final var value = UUID.randomUUID().toString();

		HeaderContext.add("x-aufrest-trace-id", value);

		final var response = factory.getBean(HeaderTestCase001.class).get();

		Assertions.assertEquals(value, response.getHeaders().get("x-aufrest-trace-id"));
		Assertions.assertEquals("3", response.getHeaders().get(AppConfig.HEADERS.get(1)));
	}

	@Test
	void headers_003() {
		HeaderContext.add("x-aufrest-trace-id", UUID.randomUUID().toString());

		final var value = UUID.randomUUID().toString();

		final var response = factory.getBean(HeaderTestCase001.class).get(value);

		Assertions.assertEquals(value, response.getHeaders().get("x-aufrest-trace-id"));
		Assertions.assertEquals("3", response.getHeaders().get(AppConfig.HEADERS.get(1)));
	}

	@Test
	void header_map_004() {
		final var bean = factory.getBean(HeaderTestCase001.class);

		final var body = bean.get(Map.of("h1", List.of("1", "2", "3"), "h2", List.of("4")));

		Assertions.assertEquals("1, 2, 3", body.getHeaders().get("h1"));
		Assertions.assertEquals("4", body.getHeaders().get("h2"));
	}
}
