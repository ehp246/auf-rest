package me.ehp246.aufrest.core.reflection;

import java.util.function.BiFunction;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ArgBinder<T, V> extends BiFunction<T, Object[], V> {
}
