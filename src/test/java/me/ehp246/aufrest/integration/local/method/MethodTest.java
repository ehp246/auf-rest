package me.ehp246.aufrest.integration.local.method;

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
class MethodTest {
	@Autowired
	private TestCase001 case001;

	@Test
	void get() {
		Assertions.assertEquals("GET", case001.get());
	}

	@Test
	void put() {
		Assertions.assertEquals("PUT", case001.put());
	}

	@Test
	void post() {
		Assertions.assertEquals("POST", case001.post());
	}

	@Test
	void patch() {
		Assertions.assertEquals("PATCH", case001.patch());
	}

	@Test
	void delete() {
		Assertions.assertEquals("DELETE", case001.delete());
	}
}
