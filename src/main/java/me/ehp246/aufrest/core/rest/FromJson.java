package me.ehp246.aufrest.core.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.JacksonTypeView;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface FromJson {
    Object fromJson(String json, JacksonTypeView typeOf);
}