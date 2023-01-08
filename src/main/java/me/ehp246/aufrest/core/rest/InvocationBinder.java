package me.ehp246.aufrest.core.rest;

import java.lang.reflect.Proxy;

import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor;

/**
 * The abstraction that turns an invocation on a {@linkplain Proxy} into a
 * {@linkplain RestRequest}. Produced by a {@linkplain ProxyMethodParser}.
 *
 * @author Lei Yang
 * @since 4.0
 */
public interface InvocationBinder {
    Bound apply(Object target, Object[] args);

    record Bound(RestRequest request, RestBodyDescriptor<?> requestBodyDescriptor, RestResponseDescriptor<?> responseDescriptor,
            ResponseReturnMapper returnMapper) {
    }
}
