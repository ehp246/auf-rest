package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Lei Yang
 * @since 4.0
 */
public interface RestBodyDescriptor<T> {
    /**
     * The declared type of the body.
     *
     */
    Class<T> type();

    /**
     * Returns the annotation if present. Otherwise, <code>null</code>.
     *
     */
    default <A extends Annotation> A annotation(final Class<A> type) {
        return null;
    }

    /**
     * The type parameters if {@linkplain #type()} is a generic {@linkplain List} or
     * {@linkplain Set}.
     *
     */
    default Class<?>[] reifying() {
        return null;
    }

    /**
     * For {@linkplain ObjectWriter#withView(Class)} and
     * {@linkplain ObjectReader#withView(Class)}.
     */
    default Class<?> view() {
        return null;
    }
}
