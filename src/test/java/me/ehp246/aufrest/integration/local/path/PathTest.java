package me.ehp246.aufrest.integration.local.path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class PathTest {
	@Autowired
	private TestCase001 case001;

	@Test
	void test_001() {
		Assertions.assertEquals("/path", case001.get());
	}
}
