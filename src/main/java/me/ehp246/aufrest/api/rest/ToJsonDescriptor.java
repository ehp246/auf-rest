package me.ehp246.aufrest.api.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Defines the details of a value object outside of the value itself. It could
 * include the serializing type and view. Used to configure
 * {@linkplain ObjectWriter} features. E.g., {@linkplain JsonView} support, and
 * declared type recognition for {@linkplain ObjectMapper#writerFor(Class)}.
 *
 * @author Lei Yang
 * @since 3.1.2
 *
 */
public interface ToJsonDescriptor {
    Class<?> type();

    default Class<?> view() {
        return null;
    }
}
