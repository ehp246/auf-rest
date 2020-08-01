package org.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface HttpFnProvider {
	HttpFn get(HttpFnConfig clientConfig);
}
