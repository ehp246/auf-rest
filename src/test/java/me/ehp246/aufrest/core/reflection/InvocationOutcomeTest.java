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
        Assertions.assertDoesNotThrow(() -> o.orElseThrow(null));
    }

    @Test
    void exception_002() {
        final var e = new IllegalArgumentException();
        final var o = InvocationOutcome.invoke((Supplier<?>) () -> {
            throw e;
        });
        Assertions.assertEquals(true, o.hasThrown());
        Assertions.assertEquals(e, o.received(), "Should propogate");
        Assertions.assertThrows(e.getClass(), () -> o.orElseThrow(null));
        Assertions.assertThrows(e.getClass(), () -> o.orElseThrow(List.of()));
    }

    @Test
    void callable_001() {
        final var e = new IOException();
        final var o = InvocationOutcome.invoke(() -> {
            throw e;
        }, null);
        Assertions.assertEquals(true, o.hasThrown());
        Assertions.assertEquals(e, o.received(), "Should have cause");
        Assertions.assertThrows(RuntimeException.class, () -> o.orElseThrow(null));
    }

    @Test
    void callable_002() {
        final var e = new IOException();
        final var o = InvocationOutcome.invoke(() -> {
            throw e;
        }, ex -> ex.getCause());
        Assertions.assertEquals(true, o.hasThrown());
        Assertions.assertEquals(null, o.received());
        Assertions.assertThrows(RuntimeException.class, () -> o.orElseThrow(null));
    }

    @Test
    void callable_003() {
        final var e = new IOException();
        final var o = InvocationOutcome.invoke((Callable<?>) () -> {
            throw e;
        });
        Assertions.assertEquals(true, o.hasThrown());
        Assertions.assertEquals(e, o.received(), "Should have cause");
        Assertions.assertThrows(IOException.class, () -> o.orElseThrow(List.of(IOException.class)));
    }
}
