package me.ehp246.aufrest.core.reflection;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Lei Yang
 *
 */
public interface InvocationOutcome {
    /**
     * The object that is either returned or thrown by the invocation. Could be null
     * or void.
     * 
     * @return
     */
    Object received();

    /**
     * Indicates the invocation didn't complete normally. It has thrown an
     * error/exception.
     * 
     * @return
     */
    default boolean hasThrown() {
        return false;
    }

    /**
     * Returns the received as the value or dispatch an exception.
     * 
     * @param canThrow
     * @return
     * @throws Throwable
     */
    default Object orElseThrow(final List<Class<?>> canThrow) throws Throwable {
        final var received = this.received();
        if (!this.hasThrown()) {
            return received;
        }

        // Dispatch the exception.
        if (received instanceof RuntimeException) {
            throw (Throwable) received;
        }
        // Checked and declared.
        if (canThrow != null && canThrow.stream().filter(c -> c.isAssignableFrom(received.getClass())).count() > 0) {
            throw (Throwable) received;
        }
        // Wrap in a RuntimeExxception.
        throw new RuntimeException((Throwable) received);
    }

    public static InvocationOutcome invoke(final Supplier<?> supplier) {
        return invoke(supplier::get, null);
    }

    public static InvocationOutcome invoke(final Callable<?> callable) {
        return invoke(callable, null);
    }

    /**
     * 
     * @param callable
     * @param map
     * @return
     */
    public static InvocationOutcome invoke(final Callable<?> callable, final Function<Throwable, Throwable> map) {
        try {
            // Call it now. Don't wait.
            final var returned = callable.call();
            return () -> returned;
        } catch (Exception e) {
            final var mapped = map == null ? e : map.apply(e);
            return new InvocationOutcome() {
                @Override
                public Object received() {
                    return mapped;
                }

                @Override
                public boolean hasThrown() {
                    return true;
                }
            };
        }
    }
}