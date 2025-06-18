package me.ehp246.aufrest.api.rest;

import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Specifies the type and view information for {@linkplain ObjectMapper} read
 * and write operations.
 * 
 * @author Lei Yang
 */
public interface TypeOfJson {
    Type type();

    default Class<?> view() {
        return null;
    }

    static TypeOfJson of(final Type type) {
        return TypeOfJson.of(type, null);
    }

    static TypeOfJson of(final Type type, final Class<?> view) {
        return new TypeOfJson() {

            @Override
            public Type type() {
                return type;
            }

            @Override
            public Class<?> view() {
                return view;
            }

        };
    }
}
