package me.ehp246.aufrest.core.rest.binder;

import java.util.List;
import java.util.Map;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface QueryBinder {
    Map<String, List<String>> aapply(Object target, Object[] args);
}
