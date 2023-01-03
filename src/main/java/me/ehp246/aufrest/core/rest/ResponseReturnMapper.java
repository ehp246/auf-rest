package me.ehp246.aufrest.core.rest;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * The abstraction to map a {@linkplain HttpResponse} to a return object.
 * Propagates exceptions according to the <code>throws</code> clause.
 *
 * @author Lei Yang
 * @see {@link ErrorResponseException}, {@link UnhandledResponseException}
 *
 */
@FunctionalInterface
public interface ResponseReturnMapper {
    Object apply(RestRequest restRequest, HttpResponse<?> httpResponse) throws ErrorResponseException;
}
