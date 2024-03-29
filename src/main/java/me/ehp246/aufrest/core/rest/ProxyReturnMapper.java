package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * The abstraction to map a {@linkplain FnOutcome} to a return value of a
 * {@linkplain ByRest} method. Should propagate exceptions according to the
 * <code>throws</code> clause of the method.
 * <p>
 * Produced by {@linkplain ProxyMethodParser}.
 *
 * @author Lei Yang
 * @see ErrorResponseException
 * @see UnhandledResponseException
 * @see DefaultProxyMethodParser
 * @see ByRestProxyFactory
 * @since 4.0
 */
@FunctionalInterface
public interface ProxyReturnMapper {
    Object apply(RestRequest restRequest, FnOutcome outcome) throws Throwable;
}
