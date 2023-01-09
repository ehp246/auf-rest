package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Lei Yang
 * @since 4.0
 */
public final class RestBodyDescriptor<T> {
    public static final RestBodyDescriptor<Map<String, Object>> MAP = new RestBodyDescriptor<Map<String, Object>>(
            Map.class, null, null);
    /**
     * The declared type of the body.
     */
    private final Class<?> type;
    /**
     * The type parameters if {@linkplain #contentType()} is a generic
     * {@linkplain List} or {@linkplain Set}.
     */
    private final Class<?>[] reifying;
    /**
     * For {@linkplain ObjectWriter#withView(Class)} and
     * {@linkplain ObjectReader#withView(Class)}.
     */
    private final Class<?> view;

    public RestBodyDescriptor(final Class<T> type) {
        this(type, null, null);
    }

    public RestBodyDescriptor(final Class<T> type, final Class<?> view) {
        this(type, null, view);
    }

    public RestBodyDescriptor(final Class<?> type, final Class<?>[] reifying, final Class<?> view) {
        this.type = type;
        this.reifying = reifying;
        this.view = view;
    }

    public Class<?> type() {
        return type;
    }

    public Class<?>[] reifying() {
        return reifying;
    }

    public Class<?> view() {
        return view;
    }
}
