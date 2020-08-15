package me.ehp246.aufrest.api.rest;

import java.net.URI;

/**
 * Defines the type of a Spring bean that applies to the global scope as the
 * default Authorization header value provider for the
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces that have
 * authorization type of
 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Type DEFAULT}.
 *
 * <p>
 * The framework calls the bean passing in the URI of the endpoint to retrieve
 * the value of Authorization header for all out-going requests of these
 * interfaces. The framework calls the bean once for each request. It does not
 * cache any value. The framework does not promise to pass in the same URI
 * object for the same interface across requests. It does promise to not pass in
 * <code>null</code>.
 *
 * <p>
 * For a given URI, the authorization provider should return the value for HTTP
 * Authorization header. The returned value is set to the header as-is with no
 * additional processing unless the value is null, blank, or empty. In these
 * cases, Authorization header will not be set.
 *
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface AuthorizationProvider {
	/**
	 *
	 * @param uri the URI for which the framework needs a value for Authorization
	 *            header.
	 * @return the header value. Null, blank, or empty indicate the header should
	 *         not be set.
	 */
	String get(URI uri);
}
