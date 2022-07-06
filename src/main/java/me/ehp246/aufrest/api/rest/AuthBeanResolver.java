package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface AuthBeanResolver {
    Object get(String name);
}
