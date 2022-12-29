package me.ehp246.aufrest.api.spi;

import java.lang.annotation.Annotation;

import me.ehp246.aufrest.api.rest.ValueDescriptor;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(Object value, ValueDescriptor valueInfo);

    default String apply(final Object value) {
        return this.apply(value, new ValueDescriptor(value.getClass(), new Annotation[] {}));
    }
}
