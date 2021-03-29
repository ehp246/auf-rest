package me.ehp246.aufrest.core.byrest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class ByRestFactoryExceptionTest {
	@Test
	void ioexception_001() {
		final var e = new IOException();
		final var factory = new ByRestFactory(cfg -> {
			return req -> {
				throw new RuntimeException(e);
			};
		}, s -> s);

		final var threw = Assertions.assertThrows(RuntimeException.class,
				factory.newInstance(ExceptionTestCase001.class)::delete);

		Assertions.assertEquals(true, threw.getCause() == e);
	}

	@Test
	void ioexception_002() {
		final var e = new IOException();
		final var factory = new ByRestFactory(cfg -> {
			return req -> {
				throw new RuntimeException(e);
			};
		}, s -> s);

		final var threw = Assertions.assertThrows(IOException.class,
				factory.newInstance(ExceptionTestCase001.class)::get);

		Assertions.assertEquals(true, threw == e);
	}
}
