package me.ehp246.aufrest.core.reflection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Lei Yang
 *
 */
class InvocationOutcomeTest {

	@Test
	void normal_001() {
		final var o = InvocationOutcome.invoke((Supplier<?>) () -> 1);
		Assertions.assertEquals(1, o.received());
		Assertions.assertEquals(false, o.hasThrown());
		Assertions.assertDoesNotThrow(() -> o.accept(null));
	}

	@Test
	void exception_002() {
		final var e = new RuntimeException();
		final var o = InvocationOutcome.invoke((Supplier<?>) () -> {
			throw e;
		});
		Assertions.assertEquals(true, o.hasThrown());
		Assertions.assertEquals(null, o.received(), "Should have no cause");
		Assertions.assertThrows(NullPointerException.class, () -> o.accept(null));
	}

	@Test
	void exception_003() {
		final var e = new NullPointerException();
		final var o = InvocationOutcome.invoke((Supplier<?>) () -> {
			throw new RuntimeException(e);
		});
		Assertions.assertEquals(true, o.hasThrown());
		Assertions.assertEquals(e, o.received(), "Should have cause");
		Assertions.assertThrows(NullPointerException.class, () -> o.accept(null));
	}

	@Test
	void callable_001() {
		final var e = new IOException();
		final var o = InvocationOutcome.invoke(() -> {
			throw e;
		}, null);
		Assertions.assertEquals(true, o.hasThrown());
		Assertions.assertEquals(e, o.received(), "Should have cause");
		Assertions.assertThrows(RuntimeException.class, () -> o.accept(null));
	}

	@Test
	void callable_002() {
		final var e = new IOException();
		final var o = InvocationOutcome.invoke(() -> {
			throw e;
		}, ex -> ex.getCause());
		Assertions.assertEquals(true, o.hasThrown());
		Assertions.assertEquals(null, o.received());
		Assertions.assertThrows(NullPointerException.class, () -> o.accept(null));
	}

	@Test
	void callable_003() {
		final var e = new IOException();
		final var o = InvocationOutcome.invoke((Callable<?>) () -> {
			throw e;
		});
		Assertions.assertEquals(true, o.hasThrown());
		Assertions.assertEquals(e, o.received(), "Should have cause");
		Assertions.assertThrows(IOException.class, () -> o.accept(List.of(IOException.class)));
	}
}
