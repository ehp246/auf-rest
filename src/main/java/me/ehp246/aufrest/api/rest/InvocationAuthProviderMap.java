package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocationAuthProviderMap {
    InvocationAuthProvider get(String name);
}
