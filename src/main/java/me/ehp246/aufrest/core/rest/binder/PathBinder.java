package me.ehp246.aufrest.core.rest.binder;

import java.util.Map;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface PathBinder {
    Bound apply(Object target, Object[] args);

    record Bound(String baseUrl, Map<String, Object> paths) {
    }
}
