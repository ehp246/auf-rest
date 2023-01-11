package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import me.ehp246.aufrest.api.exception.ErrorResponseException;

/**
 * Marker type on how to handle a response body.
 *
 * @author Lei Yang
 * @since 4.0
 */
public sealed interface BodyHandlerType<T> {
    /**
     * The Java type of the response body on a {@linkplain ErrorResponseException}.
     */
    default Class<?> errorType() {
        return Map.class;
    }

    /**
     * Defines a custom {@linkplain BodyHandler} for the response body. By-passes
     * the built-in {@linkplain InferringBodyHandlerProvider}.
     *
     * @author Lei Yang
     * @since 4.0
     */
    public final class Provided<T> implements BodyHandlerType<T> {
        public static final Provided<Void> DISCARDING = new Provided<>(BodyHandlers.discarding());

        private final BodyHandler<T> handler;
        private final Class<?> errorType;

        /**
         *
         * @param handler Required.
         */
        public Provided(final BodyHandler<T> handler) {
            this(handler, Map.class);
        }

        public Provided(final BodyHandler<T> handler, final Class<?> errorType) {
            this.handler = handler;
            this.errorType = errorType;
        }

        @Override
        public Class<?> errorType() {
            return errorType;
        }

        public BodyHandler<T> handler() {
            return handler;
        }
    }

    /**
     * Defines the typing information needed to transform the response body into a
     * Java object. Passed to {@linkplain InferringBodyHandlerProvider} for a
     * {@linkplain BodyHandler}.
     *
     * @author Lei Yang
     * @since 4.0
     * @see {@linkplain InferringBodyHandlerProvider}
     */
    public final class Inferring<T> implements BodyHandlerType<T> {
        private final BodyOf<T> bodyDescriptor;
        private final Class<?> errorType;

        public static final Inferring<Map<String, Object>> MAP = new Inferring<>(BodyOf.MAP);

        public Inferring(final BodyOf<T> body, final Class<?> errorType) {
            this.bodyDescriptor = body;
            this.errorType = errorType;
        }

        public Inferring(final BodyOf<T> descriptor) {
            this(descriptor, Map.class);
        }

        public Inferring(final Class<T> type) {
            this(new BodyOf<T>(type), Map.class);
        }

        public Inferring(final Class<T> type, final Class<?> errorType) {
            this(new BodyOf<T>(type), errorType);
        }

        @Override
        public Class<?> errorType() {
            return errorType;
        }

        public BodyOf<T> bodyOf() {
            return bodyDescriptor;
        }
    }
}