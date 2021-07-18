package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface BodyReceiver {
    /**
     * Defines the Java type of the response body. Can be a generic container, e.g.,
     * {@link List}.
     * <p>
     * This type is used to de-serialize the response body when the status code is
     * in 200 to 299 range.
     */
    Class<?> type();

    /**
     * Defines the Java type of an error response body. Can be a generic container,
     * e.g., {@link List}.
     * <p>
     * This type is used to de-serialize the response body when the status code is
     * larger than 300.
     */
    default Class<?> errorType() {
        return Object.class;
    }

    default List<Class<?>> reifying() {
        return List.of();
    }

    default List<? extends Annotation> annotations() {
        return List.of();
    }
}