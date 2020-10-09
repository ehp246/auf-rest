package me.ehp246.aufrest.integration.local.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.HeaderContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@BeforeAll
	static void clear() {
		HeaderContext.remove();
	}

	@Test
	void basic_auth_001() {
		Assertions.assertThrows(UnhandledResponseException.class,
				() -> factory.getBean(TestCases.BasicCase001.class).get());
	}

	@Test
	void basic_auth_002() {
		final var newInstance = factory.getBean(TestCases.BasicCase001.class);
		/*
		 * If the return type is HttpResponse, the invocation should not throw as long
		 * as a response is received and can be returned.
		 */
		final var response = Assertions.assertDoesNotThrow(newInstance::getAsResponse,
				"Should return a valid response instead of throwing");

		Assertions.assertEquals(401, response.statusCode(), "Should have correct status code");
	}

	@Test
	void basic_auth_003() {
		final var newInstance = factory.getBean(TestCases.BasicCase003.class);

		Assertions.assertThrows(UnhandledResponseException.class, newInstance::get,
				"Should not work because of the wrong authentication type");
	}

	@Test
	void basic_auth_004() {
		final var newInstance = factory.getBean(TestCases.BasicCase004.class);

		newInstance.get();
	}
}
