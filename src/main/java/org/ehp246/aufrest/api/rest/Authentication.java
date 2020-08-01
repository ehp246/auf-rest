package org.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
public interface Authentication {
	default String header() {
		if (this instanceof BasicAuth) {
			return ((BasicAuth) this).header();
		}
		return ((BearerToken) this).header();
	}
}
