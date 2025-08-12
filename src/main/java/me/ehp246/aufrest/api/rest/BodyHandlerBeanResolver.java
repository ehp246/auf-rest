package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.OfResponse;

/**
 * Resolves a named {@linkplain BodyHandler} specified on annotations.
 * <p>
 * At runtime, it resolves the name in Spring application context.
 *
 * @author Lei Yang
 * @see OfResponse#handler()
 */
@FunctionalInterface
public interface BodyHandlerBeanResolver {
    BodyHandler<Object> get(String name);
}
