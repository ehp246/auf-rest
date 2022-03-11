package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface BindingBodyHandlerProvider {
    BodyHandler<?> get(BindingDescriptor binding);
}
