package me.ehp246.aufrest.core.rest.binder;

import me.ehp246.aufrest.api.rest.ResponseHandler;
import me.ehp246.aufrest.core.rest.ProxyReturnMapper;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface ResponseBinder {
    Bound apply(Object target, Object[] args) throws Throwable;

    record Bound(ResponseHandler.Provided<?> provided, ProxyReturnMapper returnMapper) {
    }
}
