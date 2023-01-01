package me.ehp246.aufrest.core.byrest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.spi.DeclarationDescriptor.JsonViewDescriptor;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, JsonViewDescriptor descriptor);

    default String apply(final Object value) {
        return this.apply(value, value == null ? null : new JsonViewDescriptor(value.getClass(), null));
    }
}
