package me.ehp246.aufrest.core.reflection;

import java.util.concurrent.Callable;

public interface CatchingInvocation {
	static <T> InvocationOutcome<T> invoke(final Callable<T> callable) {
		try {
			return InvocationOutcome.returned(callable.call());
		} catch (final Exception e) {
			return InvocationOutcome.thrown(e);
		}
	}

	static InvocationOutcome<Void> invoke(final Runnable runnable) {
		try {
			runnable.run();
			return InvocationOutcome.returned(null);
		} catch (final Exception e) {
			return InvocationOutcome.thrown(e);
		}
	}
}
