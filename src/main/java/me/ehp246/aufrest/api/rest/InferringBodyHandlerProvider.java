package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * The abstraction that provides a {@linkplain BodyHandler} given a
 * {@linkplain ReturnValue} object which typically comes from a
 * {@linkplain ByRest} method return signature.
 * <p>
 * Available as a Spring bean at runtime.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface InferringBodyHandlerProvider {
    /**
     * Returns a {@linkplain BodyHandler handler} that can process both success and
     * error body.
     *
     * @param successDescriptor the descriptor on how to handle a success response.
     * @param errorDescriptor   the descriptor on how to handle an error response.
     */
    <T> BodyHandler<T> get(RestResponseDescriptor<T> descriptor);
}
