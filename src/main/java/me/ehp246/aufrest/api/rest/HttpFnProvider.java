package me.ehp246.aufrest.api.rest;

/**
 * An object that can provide a {@link HttpFn} according to
 * {@link HttpClientConfig}.
 * 
 * @author Lei Yang
 */
@FunctionalInterface
public interface HttpFnProvider {
    HttpFn get(HttpClientConfig clientConfig);
}
