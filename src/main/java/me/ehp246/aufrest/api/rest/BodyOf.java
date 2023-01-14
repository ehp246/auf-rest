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
    public static final BodyOf<Map<String, Object>> MAP = new BodyOf<Map<String, Object>>(null, Map.class);
    /**
     * The declared type of the body. The type parameters if
     * {@linkplain #contentType()} is a generic {@linkplain List} or
     * {@linkplain Set}.
     */
    private final Class<?>[] reifying;
    /**
     * For {@linkplain ObjectWriter#withView(Class)} and
     * {@linkplain ObjectReader#withView(Class)}.
     */
    private final Class<?> view;

    public BodyOf(final Class<T> type) {
        this(null, type);
    }

    /**
     * At least one type should be specified.
     */
    public BodyOf(final Class<?> view, final Class<?>... reifying) {
        if (reifying == null || reifying.length == 0 || reifying[0] == null) {
            throw new IllegalArgumentException("At least one type needed");
        }
        this.view = view;
        this.reifying = reifying;
    }

    public Class<?> type() {
        return this.reifying[0];
    }

    /**
     * Would have at least one type.
     */
    public Class<?>[] reifying() {
        return this.reifying;
    }

    public Class<?> view() {
        return view;
    }
}
