package me.ehp246.aufrest.core.rest.binder;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface HeaderBinder {
    Bound apply(Object target, Object[] args) throws Throwable;

    record Bound(Map<String, List<String>> headers, String accept, String acceptEncoding,
            Supplier<String> authSupplier) {
    }
}
