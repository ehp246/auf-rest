package me.ehp246.aufrest.api.spi;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PropertyResolver {
    String resolve(String text);
}
