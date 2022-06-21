package me.ehp246.aufrest.api.spi;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocationAuthProviderResolver {
    Object get(String name);
}
