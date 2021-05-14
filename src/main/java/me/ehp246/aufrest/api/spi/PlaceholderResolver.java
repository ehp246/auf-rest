package me.ehp246.aufrest.api.spi;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PlaceholderResolver {
    String resolve(String placeholder);
}
