package me.ehp246.aufrest.api.rest;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 * @since 1.0
 * 
 */
@FunctionalInterface
public interface RestFn {
	ResponseByRest apply(RequestByRest request);
}
