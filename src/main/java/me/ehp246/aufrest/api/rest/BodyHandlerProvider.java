package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.BodyDescriptor.ReturnValue;

/**
 * The abstraction that provides a {@linkplain BodyHandler} given a
 * {@linkplain ReturnValue} object which typically comes from a
 * {@linkplain ByRest} method return signature.
 * <p>
 * Available as a Spring bean at runtime.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface BodyHandlerProvider {
    BodyHandler<?> get(ReturnValue descriptor);
}
