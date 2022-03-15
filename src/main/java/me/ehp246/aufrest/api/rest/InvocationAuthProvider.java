package me.ehp246.aufrest.api.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.spi.Invocation;

/**
 * The abstraction of a class that can provide Authorization header value for an
 * invocation on a {@linkplain ByRest} method.
 * <p>
 * Auf REST will call the provider for each invocation once and only once before
 * the HTTP request is sent.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ByRest
 * @see AuthScheme#BEAN
 */
@FunctionalInterface
public interface InvocationAuthProvider {
    String get(Invocation invocation);
}
