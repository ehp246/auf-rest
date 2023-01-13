package me.ehp246.aufrest.core.rest;

import java.lang.reflect.Proxy;

import me.ehp246.aufrest.api.rest.BodyHandlerType;
import me.ehp246.aufrest.api.rest.BodyOf;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * The abstraction that turns an invocation on a {@linkplain Proxy} into a
 * {@linkplain RestRequest}.
 * <p>
 * Produced by a {@linkplain ProxyMethodParser}.
 *
 * @author Lei Yang
 * @since 4.0
 * @see DefaultProxyInvocationBinder
 */
public interface ProxyInvocationBinder {
    Bound apply(Object target, Object[] args);

    record Bound(RestRequest request, BodyOf<?> requestBodyDescriptor, BodyHandlerType<?> responseDescriptor,
            ResponseReturnMapper returnMapper) {
    }
}
