package me.ehp246.aufrest.integration.postman.returntype;

import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.aufrest.integration.postman.EchoResponseBody;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class })
class FutureTest {
	@Autowired
	private FutureTestCase001 case001;

	@Test
	void test_001() throws InterruptedException, ExecutionException {
		Assertions.assertEquals(true, case001.get001().get() instanceof EchoResponseBody);
	}

	@Test
	void test_002() throws InterruptedException, ExecutionException {
		final var resolved = case001.get002().get();
		Assertions.assertEquals(true, resolved instanceof HttpResponse);
		Assertions.assertEquals(true, resolved.body() instanceof EchoResponseBody);
	}
}
