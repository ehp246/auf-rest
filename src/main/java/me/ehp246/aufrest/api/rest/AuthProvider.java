package me.ehp246.aufrest.api.rest;

/**
 * Defines the type of a Spring bean that applies to the global scope as the
 * default Authorization header value provider for the
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces that have
 * authorization type of
 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme DEFAULT}.
 *
 * <p>
 * The framework calls the bean passing in resolved target URL to retrieve the
 * value of Authorization header for all out-going requests of these interfaces.
 *
 * <p>
 * The authorization provider should return the value for HTTP Authorization
 * header. The returned value is set to the header as-is with no additional
 * processing unless the value is null, blank, or empty. In these cases,
 * Authorization header will not be set.
 *
 *
 * @author Lei Yang
 */
@FunctionalInterface
public interface AuthProvider {
	/**
	 *
	 * @param uri the target resolved URL of the out-going request
	 * @return the header value. Null, blank, or empty value indicates the header
	 *         should not be set.
	 */
	String get(String uri);
}
