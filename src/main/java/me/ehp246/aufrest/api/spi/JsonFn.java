package me.ehp246.aufrest.api.spi;

import me.ehp246.aufrest.api.rest.BodyReceiver;

/**
 * @author Lei Yang
 *
 */
public interface JsonFn {
    String toJson(final Object value);

    Object fromJson(final String json, final BodyReceiver receiver);
}

