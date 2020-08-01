package org.ehp246.aufrest.api.rest;

import java.net.URI;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface AuthenticationProvider {
	Authentication get(URI uri);
}
