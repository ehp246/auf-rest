package me.ehp246.aufrest.integration.local.timeout;

import java.net.http.HttpTimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class)
@TestPropertySource(properties = { "me.ehp246.aufrest.connectTimeout=PT0.001S" })
class GlobalConnectionTimeoutTest {
	@Autowired
	private GlobalTestCase01 case001;

	/**
	 * The cause is not always <code>HttpConnectTimeoutException</code> for some reason.
	 */
	@Test
	void test_001() {
		Assertions.assertEquals(true, Assertions.assertThrows(Exception.class, case001::get)
				.getCause() instanceof HttpTimeoutException);
	}
}
