package me.ehp246.aufrest.core.reflection;

import java.util.concurrent.Callable;

/**
 * @author Lei Yang
 *
 */
public class Invocable<T> {
	private final Callable<T> callable;

	private T returned = null;
	private Throwable threw = null;

	public Invocable(final Callable<T> callable) {
		super();
		this.callable = callable;
	}

	public Invocable<T> invoke() {
		this.returned = null;
		this.threw = null;
		try {
			this.returned = this.callable.call();
		} catch (Exception e) {
			this.threw = e.getCause();
		}
		return this;
	}

	public T returned() {
		return this.returned;
	}

	public Throwable threw() {
		return this.threw;
	}

	public boolean hasThrew() {
		return this.threw != null;
	}
}
