package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.Request;
import me.ehp246.aufrest.api.rest.RequestFilter;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(MockitoExtension.class)
class JdkClientProviderFilterTest {
	@Test
	void request_filter_001() {
		final var req = new Request() {

			@Override
			public String uri() {
				return "http://nowhere";
			}
		};
		final var swappedRequest = Mockito.mock(HttpRequest.class);
		final var sentRef = new AtomicReference<HttpRequest>();
		final var filterRef = new AtomicReference<Request>();

		new JdkClientProvider(() -> MockClientBuilderSupplier.builder(sentRef)).get(new ClientConfig() {

			@Override
			public List<RequestFilter> requestFilters() {
				return List.of((httpRequest, request) -> {
					filterRef.set(request);
					return swappedRequest;
				});
			}
		}).apply(req);

		Assertions.assertEquals(true, filterRef.get() == req);
		Assertions.assertEquals(true, sentRef.get() == swappedRequest);
	}
}
