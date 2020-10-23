package me.ehp246.aufrest.integration.local.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;

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

	@Test
	void test_001() {
		Assertions.assertThrows(Exception.class, case001::m001);
	}

	@Test
	void test_002() {
		Assertions.assertEquals("GET", case001.m002());
	}

	@Test
	void test_003() {
		Assertions.assertThrows(UnhandledResponseException.class, case001::m003);
	}

	@Test
	void test_004() {
		Assertions.assertEquals("PUT", case001.put001());
	}
}
