package me.ehp246.aufrest.api.rest;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Map;

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BodyHandlerType.Inferring;
import me.ehp246.aufrest.api.rest.BodyHandlerType.Provided;

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
     * @param <T>                     The expected payload type.
     * @param request                 Required, can't be <code>null</code>.
     * @param responseBodyHnalderType Defines how to de-serialize the response body.
     *                                Both normal response and error response should
     *                                be specified. Can be <code>null</code>. For
     *                                details, see
     *                                {@linkplain InferringBodyHandlerProvider}.
     * @return The response whose {@linkplain HttpResponse#body()} has the payload
     *         transformed into a Java object as dedicated by
     *         {@linkplain BodyHandlerType}.
     */
    <T> HttpResponse<T> applyForResponse(RestRequest request, BodyHandlerType responseBodyHnalderType);

    default HttpResponse<Map<String, Object>> applyForResponse(final RestRequest request) {
        return this.applyForResponse(request, Inferring.MAP);
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
    @SuppressWarnings("unchecked")
    default <T> T apply(final RestRequest request, final Class<T> responseType) {
        return (T) this.applyForResponse(request, new Inferring<>(responseType)).body();
    }

    @SuppressWarnings("unchecked")
    default <T> T apply(final RestRequest request, final BodyHandlerType responseDescriptor) {
        return (T) this.applyForResponse(request, responseDescriptor).body();
    }

    /**
     * Executes the request and returns the response headers. Discarding the body.
     */
    default <T> HttpHeaders applyForHeaders(final RestRequest request) {
        return this.applyForResponse(request, Provided.DISCARDING).headers();
    }
}
