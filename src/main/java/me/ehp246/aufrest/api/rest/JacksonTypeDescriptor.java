package me.ehp246.aufrest.api.rest;

import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Specifies the type and view information for {@linkplain ObjectMapper} read
 * and write operations.
 * 
 * @author Lei Yang
 */
public record JacksonTypeDescriptor(Type type, Class<?> view) {
    public JacksonTypeDescriptor(final Type type) {
        this(type, null);
    }
}
