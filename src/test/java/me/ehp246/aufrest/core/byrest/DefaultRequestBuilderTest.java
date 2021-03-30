package me.ehp246.aufrest.core.byrest;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

import java.net.http.HttpRequest;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.provider.httpclient.MockRequestBuilderSupplier;

/**
 * @author Lei Yang
 *
 */
class DefaultRequestBuilderTest {
	private final AtomicReference<HttpRequest> reqRef = new AtomicReference<>();
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
			CALLS_REAL_METHODS);

	private final static String POSTMAN_ECHO = "https://postman-echo.com";
	private final static String BEARER = "I'm a bearer.";
	private final static String BASIC = "I'm basic.";
	private final AtomicReference<Integer> requestBuilderCallCountRef = new AtomicReference<>(0);

	private final AuthProvider authProvider = new AuthProvider() {
		private int count = 0;

		@Override
		public String get(final RestRequest req) {
			final var uri = req.uri();
			if (uri.toString().contains("bearer")) {
				return BEARER;
			} else if (uri.toString().contains("basic")) {
				return BASIC;
			} else if (uri.toString().contains("count")) {
				return ++count + "";
			}
			return null;
		}
	};

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
