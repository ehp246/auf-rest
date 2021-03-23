package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.RequestFilter;
import me.ehp246.aufrest.api.rest.ResponseFilter;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
class JdkProviderFilterTest {
	@Test
	void request_filter_001() {
		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};

		final var mockRequest = Mockito.mock(HttpRequest.class);
		final var map = new HashMap<>();
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		new JdkRestFnProvider(clientBuilderSupplier::builder)
				.get(new ClientConfig() {
			@Override
			public List<RequestFilter> requestFilters() {
				return List.of((httpRequest, req) -> {
					map.put("httpReq1", httpRequest);
					map.put("req1", req);
					return mockRequest;
				}, (httpRequest, req) -> {
					map.put("httpReq2", httpRequest);
					map.put("req2", req);
					return (HttpRequest) map.get("httpReq1");
				});
			}
		}).apply(req);
		
		Assertions.assertEquals(true, map.get("req1") == req);
		Assertions.assertEquals(true, map.get("req2") == req);
		Assertions.assertEquals(true, map.get("httpReq2") == mockRequest, "Should be from the first filter");
		Assertions.assertEquals(true, clientBuilderSupplier.requestSent() == map.get("httpReq1"),
				"Should send the last one");
	}
	
	@Test
	void response_filter_002() {
		final var req = new RestRequest() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};
		
		final var swap = Mockito.mock(HttpResponse.class);
		final var map = new HashMap<>();
		final var clientBuilderSupplier = new MockClientBuilderSupplier();

		final var res = new JdkRestFnProvider(clientBuilderSupplier::builder).get(new ClientConfig() {

			@Override
			public List<ResponseFilter> responseFilters() {
				return List.of((resp, req) -> {
					map.put("orig", resp);
					map.put("req1", req);
					return swap;
				}, (resp, req) -> {
					map.put("swap", resp);
					map.put("req2", req);
					return null;
				});
			}
		}).apply(req);
		
		Assertions.assertEquals(null, res.httpResponse(), "Should be the second return");
		Assertions.assertEquals(true, map.get("orig") != swap, "Should be the original");
		Assertions.assertEquals(true, map.get("swap") == swap, "Should be from the first");
		Assertions.assertEquals(true, map.get("req1") == req, "Should be the original");
		Assertions.assertEquals(true, map.get("req2") == req, "Should be the original");
	}
}
