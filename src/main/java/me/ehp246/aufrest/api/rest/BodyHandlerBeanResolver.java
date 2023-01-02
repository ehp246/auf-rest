package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * Resolves a named {@linkplain BodyHandler} specified on annotations.
 * <p>
 * At runtime, it resolves the name in Spring application context.
 *
 * @author Lei Yang
 * @see ByRest#consumerHandler()
 */
@FunctionalInterface
public interface BodyHandlerBeanResolver {
    BodyHandler<?> get(String name);
}
