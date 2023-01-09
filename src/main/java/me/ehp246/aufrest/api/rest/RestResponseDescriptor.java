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
     * @author Lei Yang
     * @since 4.0
     */
    public final class Provided<T> implements RestResponseDescriptor<T> {
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
     * Specifies how to {@linkplain BodyHandler handle} the incoming response body.
     * I.e., how to transform the body into a Java object.
     *
     * @author Lei Yang
     * @since 4.0
     * @see BodyHandler
     */
    public final class Inferring<T> implements RestResponseDescriptor<T> {
        private final RestBodyDescriptor<T> bodyDescriptor;
        private final Class<?> errorType;

        public static final Inferring<Map<String, Object>> MAP = new Inferring<>(RestBodyDescriptor.MAP);

        public Inferring(final RestBodyDescriptor<T> body, final Class<?> errorType) {
            this.bodyDescriptor = body;
            this.errorType = errorType;
        }

        public Inferring(final RestBodyDescriptor<T> descriptor) {
            this(descriptor, Map.class);
        }

        public Inferring(final Class<T> type) {
            this(new RestBodyDescriptor<T>(type), Map.class);
        }

        public Inferring(final Class<T> type, final Class<?> errorType) {
            this(new RestBodyDescriptor<T>(type), errorType);
        }

        @Override
        public Class<?> errorType() {
            return errorType;
        }

        public RestBodyDescriptor<T> body() {
            return bodyDescriptor;
        }
    }
}