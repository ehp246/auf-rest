package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

/**
 *
 * Defines the Java type of the response body. Can be a generic container, e.g.,
 * {@link List}.
 * <p>
 * This type is used to de-serialize the response body when the status code is
 * in 200 to 299 range. Defines the Java type of an error response body. Can be
 * a generic container, e.g., {@link List}.
 * <p>
 * This type is used to de-serialize the response body when the status code is
 * larger than 300.
 *
 * @author Lei Yang
 * @since 3.0
 */
public record BindingDescriptor(Class<?> type, Class<?> errorType, List<Class<?>> reifying,
        List<? extends Annotation> annotations) {
    public BindingDescriptor(final Class<?> type) {
        this(type, Object.class, List.of(), List.of());
    }

    public Class<?> firstJsonViewValue() {
        return this.annotations.stream().filter(ann -> ann.annotationType() == JsonView.class)
                .findFirst()
                .map(ann -> ((JsonView) ann).value()).map(value -> value.length == 0 ? null : value[0]).orElse(null);
    }
}