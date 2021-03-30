package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestObserver;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public class JdkRestFnProviderOberserverTest {
	private final RequestBuilder reqBuilder = req -> HttpRequest.newBuilder().build();

	@Test
	void rest_consumer_001() {
		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};

		final var map = new HashMap<>();
		final var orig = new RuntimeException("This is a test");
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		final var obs = List.of(new RestObserver() {

			@Override
			public void preSend(HttpRequest httpRequest, RestRequest req) {
				map.put("1", httpRequest);
				map.put("2", req);
			}

		}, new RestObserver() {

			@Override
			public void preSend(HttpRequest httpRequest, RestRequest req) {
				map.put("3", httpRequest);
				map.put("4", req);
			}

			@Override
			public void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
				map.put("5", exception);
			}
		});

		new DefaultRestFnProvider(clientBuilderSupplier::builder, reqBuilder, obs).get(new ClientConfig() {
		}).apply(req);

		Exception ex = null;
		try {
			new DefaultRestFnProvider(new MockClientBuilderSupplier(orig)::builder, reqBuilder, obs)
					.get(new ClientConfig() {
			}).apply(req);
		} catch (Exception e) {
			ex = e;
		}

		Assertions.assertEquals(true, map.get("1") == map.get("3"));
		Assertions.assertEquals(true, map.get("2") == map.get("4"));

		Assertions.assertEquals(true, map.get("5") == orig);
		Assertions.assertEquals(true, ex.getCause() == orig);
	}
}
