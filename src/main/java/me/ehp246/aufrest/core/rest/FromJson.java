package me.ehp246.aufrest.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.RestBodyDescriptor;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromJson {
    /**
     *
     * @param json
     * @param descriptor Required for de-serialization.
     * @return
     */
    <T> T apply(final String json, final RestBodyDescriptor<T> descriptor);
}