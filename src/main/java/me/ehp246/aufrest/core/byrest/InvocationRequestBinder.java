package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.rest.RestFn.ResponseConsumer;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public interface InvocationRequestBinder {
    Bound apply(Object target, Object[] args);

    record Bound(RestRequest request, ResponseConsumer consumer) {
    }
}
