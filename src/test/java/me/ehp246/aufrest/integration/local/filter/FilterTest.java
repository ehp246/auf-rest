package me.ehp246.aufrest.integration.local.filter;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class FilterTest {
	@Autowired
	private TestCase001 case001;

	@Test
	void test() {
		final var ret = case001.post(Instant.now());
	}
}
