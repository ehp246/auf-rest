package me.ehp246.aufrest.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lei Yang
 *
 */
public class Utils {
	private Utils() {
		super();
	}

	public static boolean hasValue(final String value) {
		return value != null && !value.isBlank();
	}

	public static Stream<String> streamValues(final Collection<String> values) {
		return Optional.ofNullable(values).orElseGet(ArrayList::new).stream().filter(Utils::hasValue);
	}

	public static List<String> listValues(final Collection<String> values) {
		return streamValues(values).collect(Collectors.toList());
	}

	public static <V> V orElse(final Callable<V> callable, final V v) {
		try {
			return callable.call();
		} catch (final Exception e) {
			return v;
		}
	}

	public static <V> V orThrow(final Callable<V> callable) {
		try {
			return callable.call();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <V> V orThrow(final Callable<V> callable, final String msg) {
		try {
			return callable.call();
		} catch (final Exception e) {
			throw new RuntimeException(msg, e);
		}
	}

	public static <V, X extends RuntimeException> V orThrow(final Callable<V> callable,
			final Function<Exception, X> fn) {
		try {
			return callable.call();
		} catch (final Exception e) {
			throw fn.apply(e);
		}
	}
}
