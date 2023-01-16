package me.ehp246.aufrest.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.BodyOf;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, BodyOf<?> descriptor);

    default String apply(final Object value) {
        return this.apply(value, null);
    }
}
