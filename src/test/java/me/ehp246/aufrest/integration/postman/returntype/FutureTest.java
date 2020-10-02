package me.ehp246.aufrest.integration.postman.returntype;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class })
class FutureTest {
	@Autowired
	private FutureTestCase001 case001;

	@Test
	void validation_001() {
	}

	@Test
	void validation_002() {
	}
}
