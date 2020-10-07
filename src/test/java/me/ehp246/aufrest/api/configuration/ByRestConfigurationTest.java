package me.ehp246.aufrest.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ByRestConfigurationTest {

	@Test
	void test_001() {
		final var clientConfig = new ByRestConfiguration().clientConfig("", "", null);

		Assertions.assertEquals(null, clientConfig.connectTimeout());
		Assertions.assertEquals(null, clientConfig.responseTimeout());
	}

	@Test
	void test_002() {
		final var clientConfig = new ByRestConfiguration().clientConfig(null, null, null);

		Assertions.assertEquals(null, clientConfig.connectTimeout());
		Assertions.assertEquals(null, clientConfig.responseTimeout());
	}

	@Test
	void test_003() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> new ByRestConfiguration().clientConfig("1", null, null));
	}

	@Test
	void test_004() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> new ByRestConfiguration().clientConfig(null, "1", null));
	}

	@Test
	void test_005() {
		final var clientConfig = new ByRestConfiguration().clientConfig("PT1S", "PT0.01S", null);

		Assertions.assertEquals(1000, clientConfig.connectTimeout().toMillis());
		Assertions.assertEquals(10, clientConfig.responseTimeout().toMillis());
	}
}
