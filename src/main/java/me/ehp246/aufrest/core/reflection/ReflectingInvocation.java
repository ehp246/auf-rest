package me.ehp246.aufrest.core.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectingInvocation {
	private final Object target;
	private final Method method;
	private final Object[] arguments;

	public ReflectingInvocation(final Object target, final Method method, final Object[] arguments) {
		super();
		this.target = target;
		this.method = method;
		this.arguments = arguments;
	}

	public static ReflectingInvocation bind(final Object target, final Method method,
			final ArgumentsProvider provider) {
		return new ReflectingInvocation(target, method, provider.provideFor(method));
	}

	/**
	 * Should never throw.
	 * 
	 * @return
	 */
	public InvocationOutcome<Object> invoke() {
		try {
			this.method.setAccessible(true);
			return InvocationOutcome.returned(this.method.invoke(target, arguments));
		} catch (InvocationTargetException e) {
			return InvocationOutcome.thrown(e.getCause());
		} catch (Exception e) {
			return InvocationOutcome.thrown(e);
		}
	}
}
