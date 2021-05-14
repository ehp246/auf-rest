package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocationAuthProvider {
    String get(Invocation invokedOn);
}
