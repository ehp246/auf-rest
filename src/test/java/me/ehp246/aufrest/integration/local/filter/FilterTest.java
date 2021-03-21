package me.ehp246.aufrest.integration.local.filter;

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
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class FilterTest {
	@Autowired
	private TestCase001 case001;
	@Autowired
	private ReqFilter reqFilter;
	@Autowired
	private RespFilter respFilter;

	@Test
	void test() {
		final var now = Instant.now();

		case001.post(now);

		Assertions.assertEquals(true, reqFilter.reqByRest().invokedOn().target() == case001);
		Assertions.assertEquals(true, reqFilter.reqByRest().invokedOn().args().get(0) == now);
	}

	@Test
	void test_002() {
		final var now = Instant.now();

		final var payload = case001.post(now);

		Assertions.assertEquals(true, respFilter.restRequest() == reqFilter.reqByRest());
		Assertions.assertEquals(true, respFilter.httpResponse().body() == payload);
	}
}
