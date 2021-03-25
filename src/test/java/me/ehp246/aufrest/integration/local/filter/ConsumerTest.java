package me.ehp246.aufrest.integration.local.filter;

import java.time.Instant;
import java.util.List;

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
class ConsumerTest {
	@Autowired
	private TestCase001 case001;
	@Autowired
	private ReqFilter reqFilter;
	@Autowired
	private RespFilter respFilter;
	@Autowired
	private List<ReqConsumer> reqConsumers;

	@Test
	void filter_001() {
		final var now = Instant.now();

		case001.post(now);

		Assertions.assertEquals(true, reqFilter.reqByRest().invokedOn().target() == case001);
		Assertions.assertEquals(true, reqFilter.reqByRest().invokedOn().args().get(0) == now);
	}

	@Test
	void filter_002() {
		final var now = Instant.now();

		final var ret = case001.post(now);

		Assertions.assertEquals(true, respFilter.restRequest() == reqFilter.reqByRest());
		Assertions.assertEquals(true, respFilter.httpResponse().body() == ret);
	}

	@Test
	void request_consumer_001() {
		final var now = Instant.now();

		case001.post(now);

		final var reqConsumer1 = reqConsumers.get(0);
		final var reqConsumer2 = reqConsumers.get(1);

		Assertions.assertEquals(true, reqConsumer1.id() == 1);
		Assertions.assertEquals(true, reqConsumer2.id() == 2);

		Assertions.assertEquals(true, reqConsumer1.httpReq() != null);
		Assertions.assertEquals(true, reqConsumer1.reqByReq() != null);
		Assertions.assertEquals(true, reqConsumer1.httpReq() == reqConsumer2.httpReq());
		Assertions.assertEquals(true, reqConsumer2.httpReq() != null);
		Assertions.assertEquals(true, reqConsumer2.reqByReq() != null);
		Assertions.assertEquals(true, reqConsumer1.reqByReq() == reqConsumer2.reqByReq());
	}
}
