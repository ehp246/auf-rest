package me.ehp246.aufrest.provider.httpclient;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ExceptionConsumer;
import me.ehp246.aufrest.api.rest.RequestConsumer;
import me.ehp246.aufrest.api.rest.ResponseConsumer;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class JdkProviderConsumerTest {
	@Test
	void request_consumer_001() {
		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};

		final var map = new HashMap<>();
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		final var config = new ClientConfig() {
			@Override
			public List<RequestConsumer> requestConsumers() {
				return List.of((a, b) -> {
					map.put("1", a);
					map.put("2", b);
				}, (a, b) -> {
					map.put("3", a);
					map.put("4", b);
				});
			}
		};

		final var restResp = new JdkRestFnProvider(clientBuilderSupplier::builder).get(config).apply(req);

		Assertions.assertEquals(true, map.get("1") == map.get("3"));
		Assertions.assertEquals(true, map.get("2") == map.get("4"));

		Assertions.assertEquals(true, restResp.httpRequest() == map.get("1"));
		Assertions.assertEquals(true, restResp.restRequest() == map.get("2"));

		Assertions.assertEquals(true, clientBuilderSupplier.requestSent() == map.get("1"), "Should send the last one");

	}
	
	@Test
	void response_consumer_001() {
		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};

		final var map = new HashMap<>();
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		final var config = new ClientConfig() {
			@Override
			public List<ResponseConsumer> responseConsumers() {
				return List.of((a, b) -> {
					map.put("1", a);
					map.put("2", b);
				}, (a, b) -> {
					map.put("3", a);
					map.put("4", b);
				});
			}
		};

		final var restResp = new JdkRestFnProvider(clientBuilderSupplier::builder).get(config).apply(req);

		Assertions.assertEquals(true, map.get("1") == map.get("3"));
		Assertions.assertEquals(true, map.get("2") == map.get("4"));

		Assertions.assertEquals(true, restResp.httpResponse() == map.get("1"));
		Assertions.assertEquals(true, restResp.restRequest() == map.get("2"));
	}

	@Test
	void exception_consumer_001() {
		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};

		final var map = new HashMap<>();
		final var orig = new RuntimeException("This is a test");
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		final var config = new ClientConfig() {
			@Override
			public List<ExceptionConsumer> exceptionConsumers() {
				return List.of((a, b) -> {
					map.put("1", a);
					map.put("2", b);
				}, (a, b) -> {
					map.put("3", a);
					map.put("4", b);
				});
			}
		};

		new JdkRestFnProvider(clientBuilderSupplier::builder).get(config).apply(req);

		Assertions.assertEquals(true, map.size() == 0);

		Exception ex = null;
		try {
			new JdkRestFnProvider(new MockClientBuilderSupplier(orig)::builder).get(config).apply(req);
		} catch (Exception e) {
			ex = e;
		}

		Assertions.assertEquals(true, map.get("1") == map.get("3"));
		Assertions.assertEquals(true, map.get("2") == map.get("4"));

		Assertions.assertEquals(true, map.get("1") == ex.getCause());
		Assertions.assertEquals(true, map.get("1") == orig);
	}
}
