package org.ehp246.aufrest.core.reflection;

@FunctionalInterface
public interface ArgumentProviderSupplier<T> {
	ArgumentsProvider get(T source);
}
