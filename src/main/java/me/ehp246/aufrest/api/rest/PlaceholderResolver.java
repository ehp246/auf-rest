package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PlaceholderResolver {
    String resolve(String placeholder);
}
