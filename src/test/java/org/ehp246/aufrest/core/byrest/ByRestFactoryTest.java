package org.ehp246.aufrest.core.byrest;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.ehp246.aufrest.api.rest.HttpFn;
import org.ehp246.aufrest.api.rest.HttpFnConfig;
import org.ehp246.aufrest.api.rest.Request;
import org.ehp246.aufrest.core.byrest.PathVariableCase001.PathObject;
import org.ehp246.aufrest.core.reflection.ObjectToText;
import org.ehp246.aufrest.core.reflection.TextToObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lei Yang
 *
 */
class ByRestFactoryTest {
	private final AtomicReference<Request> reqRef = new AtomicReference<>();

	private final HttpFn client = request -> {
		reqRef.set(request);
		return () -> null;
	};

	private final MockEnvironment env = new MockEnvironment().withProperty("echo.base", "https://postman-echo.com");

	private final ByRestFactory factory = new ByRestFactory(config -> client, env, new TextToObject() {

		@Override
		public <T> T apply(final String text, final Receiver<T> receiver) {
			return null;
		}
	}, new ObjectToText() {

		@Override
		public <T> String apply(final Supplier<T> object) {
			return null;
		}
	}, new HttpFnConfig() {
	});

	@BeforeEach
	void beforeEach() {
		reqRef.set(null);
	}

	@Test
	void get001() {
		final var newInstance = factory.newInstance(GetCase001.class);

		newInstance.get();

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get", request.uri());
		Assertions.assertEquals("GET", request.method().toUpperCase());
	}

	@Test
	void get002() {
		final var newInstance = factory.newInstance(GetCase001.class);

		newInstance.get("");

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get1", request.uri());
		Assertions.assertEquals("GET", request.method().toUpperCase());
	}

	@Test
	void get003() {
		final var newInstance = factory.newInstance(GetCase001.class);

		newInstance.get(0);

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com", request.uri());
		Assertions.assertEquals("GET", request.method().toUpperCase());
	}

	@Test
	void path001() {
		final var newInstance = factory.newInstance(PathVariableCase001.class);

		newInstance.getByPathVariable("1", "3");

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri());
	}

	@Test
	void path002() {
		final var newInstance = factory.newInstance(PathVariableCase001.class);

		newInstance.getByPathParam("4", "1", "3");

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
		final var newInstance = factory.newInstance(PathVariableCase001.class);

		newInstance.getByPathVariable("1", "3");

		final var request = reqRef.get();

		/**
		 * Method-level annotation overwrites type-level. This behavior is different
		 * from Spring's RequestMapping.
		 */
		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri(),
				"Should overwrite type-level annotation");
	}

	@Test
	void pathMap001() {
		final var newInstance = factory.newInstance(PathVariableCase001.class);

		newInstance.getByMap(Map.of("path1", "1", "path3", "3"));

		final var request = reqRef.get();

		/**
		 * Method-level annotation overwrites type-level. This behavior is different
		 * from Spring's RequestMapping.
		 */
		Assertions.assertEquals("https://postman-echo.com/get/1/path2/3", request.uri());
	}

	@Test
	void pathMap002() {
		final var newInstance = factory.newInstance(PathVariableCase001.class);

		newInstance.getByMap(Map.of("path1", "mapped1", "path3", "3"), "1");

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
		final var newInstance = factory.newInstance(PathVariableCase001.class);

		newInstance.getByObject(new PathObject() {

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

	@Test
	void requestParam001() {
		final var newInstance = factory.newInstance(RequestParamCase001.class);

		newInstance.queryByParams("q1", "q2");

		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get?query1=q1&query2=q2", request.uri());
	}

	@Test
	void requestParam002() {
		final var newInstance = factory.newInstance(RequestParamCase001.class);

		newInstance.queryEncoded("1 + 1 = 2");
		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2", request.uri(),
				"Should be encoded");
	}

	@Test
	void requestMap001() {
		final var newInstance = factory.newInstance(RequestParamCase001.class);

		newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));
		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2&query2=q2", request.uri());
	}

	@Test
	void requestMap002() {
		final var newInstance = factory.newInstance(RequestParamCase001.class);

		newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));
		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2&query2=q2", request.uri());
	}

	@Test
	void requestMap003() {
		final var newInstance = factory.newInstance(RequestParamCase001.class);

		newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"), "q3");
		final var request = reqRef.get();

		Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2&query2=q3", request.uri());
	}

	@Test
	void method001() {
		factory.newInstance(MethodTestCase001.class).get();

		Assertions.assertEquals("GET", reqRef.get().method());
	}

	@Test
	void method002() {
		factory.newInstance(MethodTestCase001.class).query();

		Assertions.assertEquals("GET", reqRef.get().method());
	}

	@Test
	void method004() {
		factory.newInstance(MethodTestCase001.class).post();

		Assertions.assertEquals("POST", reqRef.get().method());
	}

	@Test
	void method005() {
		factory.newInstance(MethodTestCase001.class).delete();

		Assertions.assertEquals(RequestMethod.DELETE.name(), reqRef.get().method());
	}

	@Test
	void method006() {
		factory.newInstance(MethodTestCase001.class).put();

		Assertions.assertEquals(RequestMethod.PUT.name(), reqRef.get().method());
	}

	@Test
	void method007() {
		factory.newInstance(MethodTestCase001.class).patch();

		Assertions.assertEquals(RequestMethod.PATCH.name(), reqRef.get().method());
	}

	@Test
	void method008() {
		factory.newInstance(MethodTestCase001.class).query();

		Assertions.assertEquals("GET", reqRef.get().method());
	}

	@Test
	void method009() {
		factory.newInstance(MethodTestCase001.class).create();

		Assertions.assertEquals("POST", reqRef.get().method());
	}

	@Test
	void method010() {
		factory.newInstance(MethodTestCase001.class).remove();

		Assertions.assertEquals("DELETE", reqRef.get().method());
	}

	@Test
	void method011() {
		factory.newInstance(MethodTestCase001.class).getBySomething();

		Assertions.assertEquals("GET", reqRef.get().method());
	}

	@Test
	void method012() {
		factory.newInstance(MethodTestCase001.class).query(1);

		Assertions.assertThrows(RuntimeException.class, reqRef.get()::method);
	}

	@Test
	void method0013() {
		factory.newInstance(MethodTestCase001.class).postByName();

		Assertions.assertEquals("POST", reqRef.get().method());
	}

}
