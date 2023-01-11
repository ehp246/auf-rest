package me.ehp246.aufrest.api.rest;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Map;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor.Inferring;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor.Provided;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 4.0
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
     * @param <T>                The expected payload type.
     * @param request
     * @param requestDescriptor  Can be <code>null</code>. In which case, the object
     *                           reference type will be used for serialization.
     * @param responseDescriptor Defines how to de-serialize the response body. Both
     *                           normal response and error response should be
     *                           specified. Can be <code>null</code>. For details,
     *                           see {@linkplain InferringBodyHandlerProvider}.
     * @return The response whose {@linkplain HttpResponse#body()} has the payload
     *         transformed into a Java object as dedicated by
     *         {@linkplain RestResponseDescriptor}.
     */
    <T> HttpResponse<T> applyForResponse(RestRequest request, RestBodyDescriptor<?> requestDescriptor,
            RestResponseDescriptor<T> responseDescriptor);

    default HttpResponse<Map<String, Object>> applyForResponse(final RestRequest request) {
        return this.applyForResponse(request, null, Inferring.MAP);
    }
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
    default HttpResponse<Map<String, Object>> applyForResponse(final RestRequest request,
            final RestBodyDescriptor<?> requestDescriptor) {
        return this.applyForResponse(request, requestDescriptor, Inferring.MAP);
    }

    default <T> HttpResponse<T> applyForResponse(final RestRequest request,
            final RestResponseDescriptor<T> responseDescriptor) {
        return this.applyForResponse(request, null, responseDescriptor);
    }

    /**
     * Executes the request and returns the response body as a {@linkplain Map}.
     */
    default Map<String, Object> apply(final RestRequest request) {
        return this.applyForResponse(request).body();
    }

    /**
     * Returns the response body de-serialized as <code>responseType</code>.
     * <p>
     * Simple Java types. No generic container types.
     */
    default <T> T apply(final RestRequest request, final Class<T> responseType) {
        return this.applyForResponse(request, null, new Inferring<>(responseType)).body();
    }

    /**
     * Executes the request and returns the response body as a {@linkplain Map}.
     */
    default Map<String, Object> apply(final RestRequest request, final RestBodyDescriptor<?> requestDescriptor) {
        return this.applyForResponse(request, requestDescriptor, Inferring.MAP).body();
    }

    default <T> T apply(final RestRequest request, final RestResponseDescriptor<T> responseDescriptor) {
        return this.applyForResponse(request, null, responseDescriptor).body();
    }

    default <T> T apply(final RestRequest request, final RestBodyDescriptor<?> requestDescriptor,
            final RestResponseDescriptor<T> responseDescriptor) {
        return this.applyForResponse(request, requestDescriptor, responseDescriptor).body();
    }

    /**
     * Executes the request and returns the response headers. Discarding the body.
     */
    default <T> HttpHeaders applyForHeaders(final RestRequest request) {
        return this.applyForResponse(request, null, Provided.DISCARDING).headers();
    }
}
