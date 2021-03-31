/**
 * 
 */
package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
class ByRestFactoryAuthTest {
	private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

	private final RestFn client = request -> {
		reqRef.set(request);
		return Mockito.mock(HttpResponse.class);
	};

	private final MockEnvironment env = new MockEnvironment().withProperty("echo.base", "https://postman-echo.com")
			.withProperty("api.bearer.token", "ec3fb099-7fa3-477b-82ce-05547babad95")
			.withProperty("postman.username", "postman1").withProperty("postman.password", "password1");

	private final ByRestFactory factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

	@BeforeEach
	void beforeEach() {
		reqRef.set(null);
	}

	@Test
	void default_001() {
		final var factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

		factory.newInstance(AuthTestCases.Case001.class).get();

		Assertions.assertEquals(null, reqRef.get().authSupplier(),
				"Should have no supplier leaving it to the global provider");
	}

	@Test
	void default_002() {
		final var factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

		factory.newInstance(AuthTestCases.Case001.class).get("");

		Assertions.assertEquals("", reqRef.get().authSupplier().get());
	}

	@Test
	void default_003() {
		final var factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

		factory.newInstance(AuthTestCases.Case001.class).get(" ");

		Assertions.assertEquals(" ", reqRef.get().authSupplier().get());
	}

	@Test
	void default_004() {
		final var factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

		factory.newInstance(AuthTestCases.Case001.class).get(null);

		Assertions.assertEquals(null, reqRef.get().authSupplier().get());
	}

	@Test
	void basic_001() {
		factory.newInstance(AuthTestCases.Case002.class).get();

		Assertions.assertEquals("Basic cG9zdG1hbjpwYXNzd29yZA==", reqRef.get().authSupplier().get());
	}

	@Test
	void basic_002() {
		factory.newInstance(AuthTestCases.Case005.class).get();

		Assertions.assertEquals("Basic cG9zdG1hbjE6cGFzc3dvcmQx", reqRef.get().authSupplier().get());
	}

	@Test
	void basic_003() {
		factory.newInstance(AuthTestCases.Case005.class).get("");

		Assertions.assertEquals("", reqRef.get().authSupplier().get());
	}

	@Test
	void basic_004() {
		factory.newInstance(AuthTestCases.Case005.class).get("  ");

		Assertions.assertEquals("  ", reqRef.get().authSupplier().get());
	}

	@Test
	void basic_005() {
		factory.newInstance(AuthTestCases.Case005.class).get(null);

		Assertions.assertEquals(null, reqRef.get().authSupplier().get());
	}

	@Test
	void case003_001() {
		factory.newInstance(AuthTestCases.Case003.class).get();

		Assertions.assertEquals("Bearer ec3fb099-7fa3-477b-82ce-05547babad95",
				reqRef.get().authSupplier().get());
	}

	@Test
	void case003_002() {
		factory.newInstance(AuthTestCases.Case003.class).get(null);

		Assertions.assertEquals(null, reqRef.get().authSupplier().get());
	}

	@Test
	void case003_003() {
		factory.newInstance(AuthTestCases.Case003.class).get("");

		Assertions.assertEquals("", reqRef.get().authSupplier().get());
	}

	@Test
	void case004_001() {
		factory.newInstance(AuthTestCases.Case004.class).get();

		Assertions.assertEquals("CustomKey custom.header.123", reqRef.get().authSupplier().get());
	}

	@Test
	void case004_002() {
		factory.newInstance(AuthTestCases.Case004.class).get("234");

		Assertions.assertEquals("234", reqRef.get().authSupplier().get());
	}

	@Test
	void case010_001() {
		factory.newInstance(AuthTestCases.Case010.class).get();

		Assertions.assertEquals(null, reqRef.get().authSupplier().get());
	}

	@Test
	void case010_002() {
		factory.newInstance(AuthTestCases.Case010.class).get(null);

		Assertions.assertEquals(null, reqRef.get().authSupplier().get());
	}

	@Test
	void case010_003() {
		factory.newInstance(AuthTestCases.Case010.class).get("null");

		Assertions.assertEquals("null", reqRef.get().authSupplier().get());
	}

	@Test
	void case_exception() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(AuthTestCases.Case007.class));
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(AuthTestCases.Case008.class));
		Assertions.assertThrows(IllegalArgumentException.class, () -> factory.newInstance(AuthTestCases.Case009.class));
	}
}
