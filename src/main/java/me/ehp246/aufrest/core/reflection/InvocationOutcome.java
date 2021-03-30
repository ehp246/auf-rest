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

	default Object accept(final List<Class<?>> types) throws Throwable {
		final var received = this.received();
		if (!this.hasThrown()) {
			return received;
		}

		// There is probably something wrong with the client code.
		if (received == null) {
			throw new NullPointerException();
		}

		if (received instanceof RuntimeException) {
			throw (Throwable) received;
		}
		// Checked and declared.
		if (types != null && types.contains(received.getClass())) {
			throw (Throwable) received;
		}
		// Unknown Throwable.
		throw new RuntimeException((Throwable) received);
	}

	/**
	 * It's assumed the supplier wraps all exceptions, checked or not, into a
	 * {@link RuntimeException}. The method only accepts {@link RuntimeException}
	 * and maps by <code>getCause</code>.
	 * 
	 * @param supplier the invocation to execute
	 * @return
	 */
	public static InvocationOutcome invoke(final Supplier<?> supplier) {
		return invoke(supplier::get, e -> e.getCause());
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