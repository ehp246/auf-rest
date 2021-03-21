package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 * @since 2.1
 *
 */
@FunctionalInterface
public interface ResponseFilter {
	ResponseByRest apply(ResponseByRest response);
}
