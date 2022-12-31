package me.ehp246.aufrest.core.byrest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.JsonBodyDescriptor;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, JsonBodyDescriptor descriptor);

    default String apply(final Object value) {
        return this.apply(value, value == null ? null : new JsonBodyDescriptor(value.getClass(), null));
    }
}
