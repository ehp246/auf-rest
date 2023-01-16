package me.ehp246.aufrest.core.reflection;

import java.util.function.Function;

/**
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ArgBinderProvider<T, V> extends Function<ReflectedParameter, ArgBinder<T, V>> {
}