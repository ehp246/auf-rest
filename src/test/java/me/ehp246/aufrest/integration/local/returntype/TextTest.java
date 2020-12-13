package me.ehp246.aufrest.integration.local.returntype;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class TextTest {
	@Autowired
	private TextTestCase001 textCase001;

	@Test
	void text_test_001() {
		final var instant = textCase001.get();

		Assertions.assertEquals(true, instant instanceof String);
		Assertions.assertDoesNotThrow(() -> Instant.parse(instant));
	}

	@Test
	void text_test_002() {
		final var now = Instant.now();
		final var returned = textCase001.post(now);

		Assertions.assertEquals(now.toString(), returned);
	}
}
