package me.ehp246.aufrest.core.byrest.returntype;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.BodyFn;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ClientFn;
import me.ehp246.aufrest.api.rest.Request;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.mock.Jackson;
import me.ehp246.aufrest.mock.MockResponse;
import me.ehp246.aufrest.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
class ReturnTypeTest {
	final ClientConfig clientConfig = new ClientConfig() {

		@Override
		public Set<BodyFn> bodyFns() {
			return Set.of(new JsonByJackson(Jackson.OBJECT_MAPPER));
		}
	};
	private final AtomicReference<Request> reqRef = new AtomicReference<>();
	private final ClientFn client = request -> {
		reqRef.set(request);
		return new MockResponse<>();
	};
	private final ByRestFactory factory = new ByRestFactory(cfg -> client, new MockEnvironment(),
			new DefaultListableBeanFactory());

	private final ReturnTypeTestCase001 case001 = factory.newInstance(ReturnTypeTestCase001.class);

	@BeforeEach
	void beforeEach() {
		reqRef.set(null);
	}

	@Test
	void return_type_001() {
		Assertions.assertThrows(IllegalArgumentException.class,
				factory.newInstance(ReturnTypeTestCase001.class)::get001);
	}

	@Test
	void return_type_002() {
		Assertions.assertThrows(IllegalArgumentException.class,
				factory.newInstance(ReturnTypeTestCase001.class)::get002);
	}

	@Test
	void return_type_003() {
		Assertions.assertThrows(Exception.class, factory.newInstance(ReturnTypeTestCase001.class)::get004);
	}

	@Test
	void return_type_004() {
		Assertions.assertThrows(Exception.class, factory.newInstance(ReturnTypeTestCase001.class)::get005);
	}

	@Test
	void receiver_001() throws Exception {
		case001.get003().get();

		final var bodyReceiver = reqRef.get().bodyReceiver();

		Assertions.assertEquals(List.class, bodyReceiver.type());

		Assertions.assertEquals(Instant.class, bodyReceiver.reifying().get(0));
	}

	@Test
	void receiver_002() throws Exception {
		final var typeRef = new ParameterizedTypeReference<List<Instant>>() {
		};

		final var t = (ParameterizedType) typeRef.getClass().getGenericSuperclass();
		final Type[] actualTypeArguments = t.getActualTypeArguments();

		case001.get006(typeRef);

		final var bodyReceiver = reqRef.get().bodyReceiver();

		Assertions.assertEquals(List.class, bodyReceiver.type());

		Assertions.assertEquals(Instant.class, bodyReceiver.reifying().get(0));
	}

}
