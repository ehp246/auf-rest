/**
 * 
 */
package me.ehp246.aufrest.integration.local.xml;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@Disabled
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class XmlTest {
	@Autowired
	private TestCase001 case001;

	@Test
	void test_001() {
		final var count = (int) (Math.random() * 10);
		List<Instant> instants;
		try {
			instants = case001.get001(count);
		} catch (final Exception e) {
			throw e;
		}

		Assertions.assertEquals(count, instants.size());

		instants.stream().forEach(instant -> Assertions.assertEquals(true, instant instanceof Instant));
	}

	@Test
	void test_008() {
		try {
			case001.get008();
		} catch (final Exception e) {
			throw e;
		}
	}
}
