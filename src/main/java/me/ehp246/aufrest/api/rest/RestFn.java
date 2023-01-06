package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;
import java.util.Map;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface RestFn {
    /**
     * Returns the response when and only when one is received and its status code
     * is within 200 - 299.
     * <p>
     * If the status code is larger than 299, a
     * {@linkplain UnhandledResponseException} will be raised.
     *
     * @param <T>                    The expected payload type.
     * @param request
     * @param requestBodyDescriptor  Can be <code>null</code>. In which case, the
     *                               object reference type will be used for
     *                               serialization.
     * @param restResponseDescriptor Defines how to de-serialize the response body.
     *                               Both normal response and error response should
     *                               be specified. Can be <code>null</code>. In
     *                               which case, the response body will be
     *                               de-serialized on a best-effort approach based
     *                               on the <code>content-type</code> header. For
     *                               JSON types, it will be de-serialized to a
     *                               {@linkplain Map Map&lt;String, Object;&gt;}.
     *                               Other content types will be accepted as raw
     *                               {@linkplain String}.
     * @return The response whose {@linkplain HttpResponse#body()} has the payload
     *         transformed into a Java object as dedicated by
     *         {@linkplain RestResponseDescriptor}.
     */
    <T> HttpResponse<T> apply(RestRequest request, RestBodyDescriptor<?> requestBodyDescriptor,
            RestResponseDescriptor<T> restResponseDescriptor);

    /**
     * Sends the request body by inferring on the reference type of
     * {@linkplain RestRequest#body()}.
     * <p>
     * De-serializes the response body on a best-effort approach based on the
     * incoming <code>content-type</code> header. For JSON types, the body will be
     * de-serialized to a {@linkplain Map Map&lt;String, Object;&gt;}. Otherwise,
     * the body will be accepted as raw {@linkplain String}.
     *
     */
    default <T> HttpResponse<T> apply(final RestRequest request) {
        return this.apply(request, null, null);
    }

    default <T> HttpResponse<T> apply(final RestRequest request, final RestBodyDescriptor<?> requestBodyDescriptor) {
        return this.apply(request, requestBodyDescriptor, null);
    }

    default <T> HttpResponse<T> apply(final RestRequest request,
            final RestResponseDescriptor<T> restResponseDescriptor) {
        return this.apply(request, null, restResponseDescriptor);
    }
}
