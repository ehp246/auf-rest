package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.util.Map;

import me.ehp246.aufrest.api.exception.ErrorResponseException;

/**
 *
 * @author Lei Yang
 * @since 4.0
 */
public sealed interface RestResponseDescriptor<T> {
    /**
     * The Java type of the response body on a {@linkplain ErrorResponseException}.
     */
    default Class<?> errorType() {
        return Map.class;
    }

    /**
     *
     * @author Lei Yang
     * @since 4.0
     */
    non-sealed interface CustomHandlerDescriptor<T> extends RestResponseDescriptor<T> {
        BodyHandler<T> handler();
    }

    /**
     * Specifies how to {@linkplain BodyHandler handle} the incoming response body.
     * I.e., how to transform the body into a Java object.
     *
     * @author Lei Yang
     * @since 4.0
     * @see BodyHandler
     */
    non-sealed interface InferringDescriptor<T> extends RestResponseDescriptor<T>, RestBodyDescriptor<T> {
    }
}