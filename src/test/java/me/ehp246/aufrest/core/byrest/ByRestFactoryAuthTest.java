/**
 * 
 */
package me.ehp246.aufrest.core.byrest;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.mock.MockResponse;

/**
 * @author Lei Yang
 *
 */
public class ByRestFactoryAuthTest {
	private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

	private final RestFn client = request -> {
		reqRef.set(request);
		return new MockResponse(request);
	};

	private final MockEnvironment env = new MockEnvironment().withProperty("echo.base", "https://postman-echo.com")
			.withProperty("api.bearer.token", "ec3fb099-7fa3-477b-82ce-05547babad95")
			.withProperty("postman.username", "postman").withProperty("postman.password", "password");

	private final ByRestFactory factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

	@BeforeEach
	void beforeEach() {
		reqRef.set(null);
	}

	@Test
	void auth_none_001() {
		final var factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

		factory.newInstance(AuthTestCases.Case001.class).get();

		Assertions.assertEquals(null, reqRef.get().authSupplier());
	}

	@Test
	void auth_global_001() {
		final var factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

		factory.newInstance(AuthTestCases.Case001.class).get();

		Assertions.assertEquals(null, reqRef.get().authSupplier(), "Should be un-aware the global provider");
	}

	@Test
	void auth_basic_001() {
		factory.newInstance(AuthTestCases.Case002.class).get();

		Assertions.assertEquals(HttpUtils.basicAuth("postman", "password"), reqRef.get().authSupplier().get());
	}

	@Test
	void auth_basic_002() {
		factory.newInstance(AuthTestCases.Case005.class).get();

		Assertions.assertEquals(HttpUtils.basicAuth("postman", "password"), reqRef.get().authSupplier().get());
	}

	@Test
	void auth_003() {
		factory.newInstance(AuthTestCases.Case003.class).get();

		Assertions.assertEquals(HttpUtils.bearerToken("ec3fb099-7fa3-477b-82ce-05547babad95"),
				reqRef.get().authSupplier().get());
	}

	@Test
	void auth_custom_001() {
		factory.newInstance(AuthTestCases.Case004.class).get();

		Assertions.assertEquals("CustomKey custom.header.123", reqRef.get().authSupplier().get());
	}

	@Test
	void auth_header_001() {
		final var value = UUID.randomUUID().toString();
		factory.newInstance(AuthTestCases.Case001.class).get(value);

		Assertions.assertEquals(value, reqRef.get().authSupplier().get());
	}

	@Test
	void auth_header_002() {
		factory.newInstance(AuthTestCases.Case001.class).get(null);

		Assertions.assertEquals(true, reqRef.get().authSupplier() != null, "Should be a non-null supplier");
		Assertions.assertEquals(null, reqRef.get().authSupplier().get(), "Should return null value for the header");
	}

	@Test
	void auth_header_003() {
		factory.newInstance(AuthTestCases.Case001.class).get("");

		Assertions.assertEquals(true, reqRef.get().authSupplier() != null, "Should be a non-null supplier");
		Assertions.assertEquals(null, reqRef.get().authSupplier().get(), "Should return null value for the header");
	}

	@Test
	void auth_header_004() {
		factory.newInstance(AuthTestCases.Case001.class).get("   	");

		Assertions.assertEquals(true, reqRef.get().authSupplier() != null, "Should be a non-null supplier");
		Assertions.assertEquals(null, reqRef.get().authSupplier().get(), "Should return null value for the header");
	}

}
