package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

/**
 * Defines the type of a Spring bean that applies to the global scope as the
 * default Authorization header value provider for the
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces that have
 * authorization scheme of
 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme DEFAULT}.
 *
 * <p>
 * The framework calls the bean passing in the out-going {@link RestRequest}
 * before a {@link HttpRequest} is built. The framework calls the bean once for
 * each request. It does not cache any value.
 *
 * <p>
 * The authorization provider should return the value for HTTP Authorization
 * header. The returned value is set to the header as-is with no additional
 * processing unless the value is null. In which case, Authorization will not be
 * set.
 *
 *
 * @author Lei Yang
 * 
 */
@FunctionalInterface
public interface AuthProvider {
    /**
     *
     * @param req the out-going request
     * 
     * @return the header value. <code>null</code> indicates the header should not
     *         be set.
     */
    String get(RestRequest req);
}
