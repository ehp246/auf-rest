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
public sealed interface BodyHandlerType {
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
    public final class Provided<T> implements BodyHandlerType {
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
     * @see InferringBodyHandlerProvider
     */
    public final class Inferring<T> implements BodyHandlerType {
        private final TypeOfJson bodyDescriptor;
        private final Class<?> errorType;

        public static final Inferring<Map<String, Object>> MAP = new Inferring<>(
                TypeOfJson.of(ParameterizedTypeBuilder.ofMap(String.class, Object.class)));

        public Inferring(final TypeOfJson bodyType, final Class<?> errorType) {
            this.bodyDescriptor = bodyType;
            this.errorType = errorType;
        }

        public Inferring(final TypeOfJson descriptor) {
            this(descriptor, Map.class);
        }

        public Inferring(final Class<T> type) {
            this(TypeOfJson.of(type), Map.class);
        }

        public Inferring(final Class<T> type, final Class<?> errorType) {
            this(TypeOfJson.of(type), errorType);
        }

        @Override
        public Class<?> errorType() {
            return errorType;
        }

        public TypeOfJson bodyType() {
            return bodyDescriptor;
        }
    }
}