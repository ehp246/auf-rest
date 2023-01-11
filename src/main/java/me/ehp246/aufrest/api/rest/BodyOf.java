package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Provides typing information to process request and response body.
 *
 * @author Lei Yang
 * @since 4.0
 */
public final class BodyOf<T> {
    public static final BodyOf<Map<String, Object>> MAP = new BodyOf<Map<String, Object>>(
            Map.class, null, (Class<?>[]) null);
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

    public BodyOf(final Class<T> type) {
        this(type, null);
    }

    public BodyOf(final Class<T> type, final Class<?> view) {
        this(type, view, (Class<?>[]) null);
    }

    public BodyOf(final Class<?> type, final Class<?> view, final Class<?>... reifying) {
        this.type = type;
        this.view = view;
        this.reifying = reifying;
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
