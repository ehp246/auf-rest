package me.ehp246.aufrest.core.byrest.timeout;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.Request;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.mock.MockResponse;

/**
 * @author Lei Yang
 *
 */
class TimeoutTest {
	private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
	private final MockEnvironment env = new MockEnvironment().withProperty("api.timeout.5s", "PT5S")
			.withProperty("api.timeout.illegal", "5");

	private final AtomicReference<Request> reqRef = new AtomicReference<>();
	private final Supplier<ClientFn> clientSupplier = () -> request -> {
		reqRef.set(request);
		return new MockResponse<>();
	};

	private final ByRestFactory factory = new ByRestFactory(clientSupplier, env, beanFactory);

	@Test
	void timeout_001() {
		factory.newInstance(TestCase001.class).get();

		Assertions.assertEquals(null, reqRef.get().timeout());
	}

	@Test
	void timeout_002() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase002.class));
	}

	@Test
	void timeout_003() {
		factory.newInstance(TestCase003.class).get();

		Assertions.assertEquals(11021, reqRef.get().timeout().toMillis());
	}

	@Test
	void timeout_004() {
		factory.newInstance(TestCase004.class).get();

		Assertions.assertEquals(5, reqRef.get().timeout().toSeconds());
	}

	@Test
	void timeout_005() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase005.class));
	}

	@Test
	void timeout_006() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(TestCase006.class));
	}

	@Test
	void timeout_007() {
		factory.newInstance(TestCase007.class).get();

		Assertions.assertEquals(10, reqRef.get().timeout().toMillis());
	}
}
