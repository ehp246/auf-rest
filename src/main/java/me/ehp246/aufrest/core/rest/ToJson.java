package me.ehp246.aufrest.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.RestBodyDescriptor;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, RestBodyDescriptor<?> descriptor);

    default String apply(final Object value) {
        return this.apply(value, null);
    }
}
