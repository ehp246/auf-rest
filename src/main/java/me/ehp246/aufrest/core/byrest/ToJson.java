package me.ehp246.aufrest.core.byrest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.spi.ValueDescriptor.JsonViewValue;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, JsonViewValue descriptor);

    default String apply(final Object value) {
        return this.apply(value, value == null ? null : new JsonViewValue(value.getClass(), null));
    }
}
