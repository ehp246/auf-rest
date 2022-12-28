package me.ehp246.aufrest.api.spi;

import me.ehp246.aufrest.api.rest.ValueDescriptor;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, ValueDescriptor valueInfo);

    default String apply(Object value) {
        return this.apply(value, null);
    }
}
