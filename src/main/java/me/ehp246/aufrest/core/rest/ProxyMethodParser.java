package me.ehp246.aufrest.core.rest;

import java.lang.reflect.Method;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * The abstraction that parses a method of a {@linkplain ByRest} interface to
 * produce a {@linkplain InvocationBinder} which in turn can turn an
 * invocation on the method to a {@linkplain RestRequest}.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProxyMethodParser {
    /**
     * The binder is derived from the method and doesn't not change once created.
     *
     * @param method must come from a {@linkplain ByRest}-annotated interface
     *
     */
    InvocationBinder parse(Method method);
}
