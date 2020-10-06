package me.ehp246.aufrest.integration.local.timeout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class TimeoutTest {
	private static Logger LOGGER = LogManager.getLogger();

	@Autowired
	private TestCase001 case001;

	@Test
	void test_001() {
	}
}
