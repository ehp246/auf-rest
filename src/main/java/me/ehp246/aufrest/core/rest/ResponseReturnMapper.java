package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * The abstraction to map a {@linkplain RestFnOutcome} to a return value of a
 * {@linkplain ByRest} method. Should propagate exceptions according to the
 * <code>throws</code> clause of the method.
 * <p>
 * Produced by {@linkplain ProxyMethodParser}.
 *
 * @author Lei Yang
 * @see {@link ErrorResponseException}, {@link UnhandledResponseException},
 *      {@linkplain DefaultProxyMethodParser}, {@linkplain ByRestProxyFactory}
 * @since 4.0
 */
@FunctionalInterface
public interface ResponseReturnMapper {
    Object apply(RestRequest restRequest, RestFnOutcome outcome) throws Throwable;
}
