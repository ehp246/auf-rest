package me.ehp246.aufrest.core.reflection;

import java.lang.reflect.Method;

@FunctionalInterface
public interface ArgumentsProvider {
	Object[] provideFor(Method method);
}
