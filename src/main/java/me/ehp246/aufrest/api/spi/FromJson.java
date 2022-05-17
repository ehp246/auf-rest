package me.ehp246.aufrest.api.spi;

import me.ehp246.aufrest.api.rest.BindingDescriptor;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromJson {
    Object apply(final String json, final BindingDescriptor receiver);
}