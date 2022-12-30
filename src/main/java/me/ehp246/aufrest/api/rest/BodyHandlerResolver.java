package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * Resolves a named {@linkplain BodyHandler}.
 *
 * @author Lei Yang
 * @see ByRest#responseBodyHandler()
 */
@FunctionalInterface
public interface BodyHandlerResolver {
    BodyHandler<Object> get(String name);
}
