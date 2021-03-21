package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.ReqByRest;
import me.ehp246.aufrest.api.rest.RequestFilter;
import me.ehp246.aufrest.api.rest.ResponseFilter;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
class JdkClientProviderFilterTest {
	@Test
	void request_filter_001() {
		final var req = new ReqByRest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};
		final var swappedRequest = Mockito.mock(HttpRequest.class);
		final var sentRef = new AtomicReference<HttpRequest>();
		final var filter1Ref = new AtomicReference<ReqByRest>();
		final var filter2Ref = new AtomicReference<HttpRequest>();

		new JdkClientProvider(() -> MockClientBuilderSupplier.builder(sentRef)).get(new ClientConfig() {

			@Override
			public List<RequestFilter> requestFilters() {
				return List.of((httpRequest, req) -> {
					filter1Ref.set(req);
					return null;
				}, (httpRequest, req) -> {
					filter2Ref.set(httpRequest);
					return swappedRequest;
				});
			}
		}).apply(req);
		
		Assertions.assertEquals(true, filter1Ref.get() == req);
		Assertions.assertEquals(true, sentRef.get() == swappedRequest);
		
		Assertions.assertEquals(true, filter2Ref.get() == null, "Should be the returned object from the first filter.");
	}
	
	@Test
	void response_filter_002() {
		final var req = new ReqByRest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};
		
		final var orig = Mockito.mock(HttpResponse.class);
		final var swap = Mockito.mock(HttpResponse.class);
		final var map = new HashMap<>();

		final var res = new JdkClientProvider(() -> MockClientBuilderSupplier.builder(new AtomicReference<HttpRequest>(), orig)).get(new ClientConfig() {

			@Override
			public List<ResponseFilter> responseFilters() {
				return List.of((httpResponse, req) -> {
					map.put("orig", httpResponse);
					map.put("req1", req);
					return swap;
				}, (httpResponse, req) -> {
					map.put("swap", httpResponse);
					map.put("req2", req);
					return null;
				});
			}
		}).apply(req);
		
		Assertions.assertEquals(null, res, "Should be the second return");
		Assertions.assertEquals(swap, map.get("swap"), "Should be from the first");
		Assertions.assertEquals(orig, map.get("orig"), "Should be the original");
		Assertions.assertEquals(req, map.get("req1"), "Should be the original");
		Assertions.assertEquals(req, map.get("req2"), "Should be the original");
	}
}
