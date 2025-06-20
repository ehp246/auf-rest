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
     * 
     * @param descriptor Can be <code>null</code>. In which case, the type will be
     *                   retrieved by {@linkplain Object#getClass()}.
     */
    String toJson(Object value, JacksonTypeDescriptor descriptor);

    default String toJson(final Object value) {
        return this.toJson(value, null);
    }
}
