package me.ehp246.aufrest.core.util;

import java.util.concurrent.Callable;

/**
 * @author Lei Yang
 *
 */
public class InvocationUtil {

	private InvocationUtil() {
		super();
	}

	public static <V> V invoke(final Callable<V> callable) {
		try {
			return callable.call();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <V> V invoke(final Callable<V> callable, final String message) {
		try {
			return callable.call();
		} catch (final Exception e) {
			throw new RuntimeException(message, e);
		}
	}
}
