package me.ehp246.aufrest.api.rest;

import me.ehp246.aufrest.api.spi.Invocation;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocationAuthProvider {
    String get(Invocation invokedOn);
}
