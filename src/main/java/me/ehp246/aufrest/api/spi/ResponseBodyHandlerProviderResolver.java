package me.ehp246.aufrest.api.spi;

import me.ehp246.aufrest.api.rest.BodyHandlerProvider;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ResponseBodyHandlerProviderResolver {
    BodyHandlerProvider get(String name);
}
