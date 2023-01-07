package me.ehp246.aufrest.api.rest;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Lei Yang
 * @since 4.0
 */
public record RestBodyDescriptor<T> (
        /**
         * The declared type of the body.
         */
        Class<?> type,
        /**
         * The type parameters if {@linkplain #contentType()} is a generic {@linkplain List} or
         * {@linkplain Set}.
         */
        Class<?>[] reifying,
        /**
         * For {@linkplain ObjectWriter#withView(Class)} and
         * {@linkplain ObjectReader#withView(Class)}.
         */
        Class<?> view) {

    public static final RestBodyDescriptor<Map<String, Object>> MAP = new RestBodyDescriptor<Map<String, Object>>(
            Map.class, null, null);

    public RestBodyDescriptor(final Class<T> type) {
        this(type, null, null);
    }

    public RestBodyDescriptor(final Class<T> type, final Class<?> view) {
        this(type, null, view);
    }
}
