package me.ehp246.aufrest.api.spi;

import me.ehp246.aufrest.api.rest.BindingDescriptor;

/**
 * @author Lei Yang
 *
 */
public interface JsonFn {
    String toJson(final Object value);

    Object fromJson(final String json, final BindingDescriptor receiver);
}

