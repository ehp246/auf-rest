package me.ehp246.aufrest.integration.local.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.HeaderContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("authProvider")
class AuthProviderTest {
	@Autowired
	private AutowireCapableBeanFactory factory;

	@BeforeAll
	static void clear() {
		HeaderContext.clear();
	}

	@Test
	void basic_auth_001() {
		final var case001 = factory.getBean(TestCases.BasicCase001.class);

		case001.get();

		/**
		 * Should throw on the second call because the global authentication provider
		 * allows only one call;
		 */
		Assertions.assertThrows(UnhandledResponseException.class, case001::get);

		/**
		 * Should work with the right header.
		 */
		case001.get("Basic YmFzaWN1c2VyOnBhc3N3b3Jk");

		/**
		 * Should not work with the wrong header.
		 */
		Assertions.assertThrows(UnhandledResponseException.class, () -> case001.get(""));
	}

	@Test
	void basic_auth_002() {
		final var case002 = factory.getBean(TestCases.BasicCase002.class);

		/**
		 * Should work on the first call.
		 */
		case002.get();
		
		/**
		 * Should work on the second call since the global provider is by-passed.
		 */
		case002.get();
	}
}
