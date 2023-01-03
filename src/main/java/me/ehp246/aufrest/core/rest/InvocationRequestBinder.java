package me.ehp246.aufrest.core.rest;

import java.lang.reflect.Proxy;
import java.net.http.HttpResponse;
import java.util.function.Function;

import me.ehp246.aufrest.api.rest.RestFn.ResponseConsumer;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * The abstraction that turns an invocation on a {@linkplain Proxy} into a
 * {@linkplain RestRequest}. Produced by a {@linkplain ProxyMethodParser}.
 *
 * @author Lei Yang
 *
 */
public interface InvocationRequestBinder {
    Bound apply(Object target, Object[] args);

    record Bound(RestRequest request, ResponseConsumer consumer, Function<HttpResponse<?>, ?> returnMapper) {
    }
}
