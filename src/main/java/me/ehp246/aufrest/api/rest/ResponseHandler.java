package me.ehp246.aufrest.api.rest;

import java.lang.reflect.Type;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import me.ehp246.aufrest.api.exception.ErrorResponseException;

/**
 * Marker type on how to handle a response.
 *
 * @author Lei Yang
 * @since 4.0
 */
public sealed interface ResponseHandler {
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
    public final class Provided<T> implements ResponseHandler {
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
     * Defines the type information needed to transform the response body into a
     * Java object. Passed to {@linkplain InferringBodyHandlerProvider} for a
     * {@linkplain BodyHandler}.
     *
     * @author Lei Yang
     * @since 4.0
     * @see InferringBodyHandlerProvider
     */
    public final class Inferring implements ResponseHandler, JacksonTypeDescriptor {
        public static final Inferring MAP = new Inferring(ParameterizedTypeBuilder.ofMap(String.class, Object.class),
                null, Map.class);

        private final Type type;
        private final Class<?> view;
        private final Class<?> errorType;

        public Inferring(Type type, Class<?> view, Class<?> errorType) {
            super();
            this.type = type;
            this.view = view;
            this.errorType = errorType == null ? Map.class : errorType;
        }

        public Inferring(final Type type) {
            this(type, null, null);
        }

        public Inferring(final Type type, final Class<?> view) {
            this(type, view, null);
        }

        @Override
        public Class<?> errorType() {
            return errorType;
        }

        @Override
        public Type type() {
            return this.type;
        }

        @Override
        public Class<?> view() {
            return this.view;
        }

    }
}