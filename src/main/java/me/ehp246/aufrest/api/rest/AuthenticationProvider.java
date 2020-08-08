package me.ehp246.aufrest.api.rest;

import java.net.URI;

/**
 * For a given URI, the authentication header provider should provide the value
 * for HTTP Auth header. The provided value is used as-is. Unless the
 * value is null, blank, or empty, in which case, no Auth header will
 * be set.
 *
 * The framework calls the provider once for each out-going HTTP request.
 *
 * The framework does not promise to pass the same URI object for the same URL
 * endpoint across requests.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface AuthenticationProvider {
	String get(URI uri);
}
