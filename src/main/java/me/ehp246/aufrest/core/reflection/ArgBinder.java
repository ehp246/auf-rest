package me.ehp246.aufrest.core.reflection;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ArgBinder<T, V> {
    V apply(T t, Object[] args) throws Throwable;
}
