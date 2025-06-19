package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;

/**
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ToJson {
    /**
     * Should return <code>null</code> for <code>null</code> reference.
     */
    String toJson(Object value, JacksonTypeDescriptor typeOf);

    default String toJson(final Object value) {
        return this.toJson(value, value == null ? null : new JacksonTypeDescriptor(value.getClass()));
    }
}
