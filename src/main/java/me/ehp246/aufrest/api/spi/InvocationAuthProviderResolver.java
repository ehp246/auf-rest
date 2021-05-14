package me.ehp246.aufrest.api.spi;

import me.ehp246.aufrest.api.rest.InvocationAuthProvider;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocationAuthProviderResolver {
    InvocationAuthProvider get(String name);
}
