package me.ehp246.aufrest.core.byrest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.spi.ValueDescriptor.ReturnValue;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromJson {
    Object apply(final String json, final ReturnValue descriptor);
}