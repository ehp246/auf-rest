package me.ehp246.aufrest.integration.postman.returntype;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufrest.integration.postman.EchoResponseBody;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class })
class HttpResponseTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@Test
	void test_003() {
		final var returned = factory.getBean(HttpResponseTestCase001.class).get003();

		Assertions.assertEquals(true, returned instanceof HttpResponse);
		Assertions.assertEquals(true, returned.body() instanceof EchoResponseBody);
	}

	@Test
	void test_004() {
		final var returned = factory.getBean(HttpResponseTestCase001.class).get004();

		Assertions.assertEquals(true, returned instanceof HttpResponse);
		Assertions.assertEquals(true, returned.body() instanceof EchoResponseBody);
	}

	@Test
	void test_005() {
		final var returned = factory.getBean(HttpResponseTestCase001.class).get005();

		Assertions.assertEquals(true, returned instanceof HttpResponse);
		Assertions.assertThrows(ClassCastException.class, () -> {
			final String body = returned.body();
		});
	}

	@Test
	void test_006() {
		final var returned = factory.getBean(HttpResponseTestCase001.class).get005();

		Assertions.assertEquals(true, returned instanceof HttpResponse);
		Assertions.assertThrows(ClassCastException.class, () -> {
			final String body = returned.body();
		});
	}

	@Test
	void test_007() {
		final var returned = factory.getBean(HttpResponseTestCase001.class).get006();

		Assertions.assertEquals(true, returned instanceof HttpResponse);
		Assertions.assertEquals(true, returned.body() instanceof String);
	}
}
