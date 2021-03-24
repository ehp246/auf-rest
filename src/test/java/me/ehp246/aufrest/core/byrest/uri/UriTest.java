package me.ehp246.aufrest.core.byrest.uri;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.env.MockEnvironment;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.core.byrest.uri.TestCase001.PathObject;
import me.ehp246.aufrest.mock.MockResponse;

/**
 * @author Lei Yang
 *
 */
class UriTest {
	private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

	private final RestFn client = request -> {
		reqRef.set(request);
		return new MockResponse(request);
	};

	private final ByRestFactory factory = new ByRestFactory(clientCfg -> client,
			new MockEnvironment().withProperty("echo.base", "https://postman-echo.com"),
			new DefaultListableBeanFactory());

	final TestCase001 case001 = factory.newInstance(TestCase001.class);

	@BeforeEach
	void beforeEach() {
		reqRef.set(null);
	}

	@Test
	void path001() {
		case001.getByPathVariable("1", "3");

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri());
	}

	@Test
	void path002() {
		case001.getByPathParam("4", "1", "3");

		final var request = reqRef.get();

		/**
		 * Method-level annotation overwrites type-level. This behavior is different
		 * from Spring's RequestMapping.
		 */
		Assertions.assertEquals("https://postman-echo.com/3/4", request.uri(),
				"Should overwrite type-level annotation");
	}

	@Test
	void path003() {
		case001.getByPathVariable("1", "3");

		final var request = reqRef.get();

		/**
		 * Method-level annotation overwrites type-level. This behavior is different
		 * from Spring's RequestMapping.
		 */
		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri(),
				"Should overwrite type-level annotation");
	}

	@Test
	void uri_004() {
		case001.getWithPlaceholder();

		Assertions.assertEquals("https://postman-echo.com/get", reqRef.get().uri());
	}

	@Test
	void uri_005() {
		case001.get001();

		Assertions.assertEquals("https://postman-echo.com/", reqRef.get().uri());
	}

	@Test
	void pathMap001() {
		case001.getByMap(Map.of("path1", "1", "path3", "3"));

		final var request = reqRef.get();

		/**
		 * Method-level annotation overwrites type-level. This behavior is different
		 * from Spring's RequestMapping.
		 */
		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri());
	}

	@Test
	void pathMap002() {
		case001.getByMap(Map.of("path1", "mapped1", "path3", "3"), "1");

		final var request = reqRef.get();

		/**
		 * Explicit parameter takes precedence.
		 */
		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri());
	}

	/*
	 * TODO
	 */
	// @Test
	void pathObject001() {
		case001.getByObject(new PathObject() {

			@Override
			public String getPath3() {
				return "3";
			}

			@Override
			public String getPath1() {
				return "1";
			}
		});

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri());
	}
}
