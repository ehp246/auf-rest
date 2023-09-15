package me.ehp246.aufrest.core.reflection;

import me.ehp246.aufrest.api.annotation.AuthBean;

/**
 * Allows application code to throw checked exception during a binding
 * operation. E.g., {@linkplain AuthBean.Invoking} methods.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ArgBinder<T, V> {
    V apply(T t, Object[] args) throws Throwable;
}
