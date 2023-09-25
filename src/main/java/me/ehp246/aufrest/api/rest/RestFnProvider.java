package me.ehp246.aufrest.api.rest;

import me.ehp246.aufrest.provider.httpclient.DefaultRestFnProvider;

/**
 * An object that can provide a {@link RestFn} according to
 * {@link RestFnConfig}.
 *
 * @author Lei Yang
 * @see DefaultRestFnProvider
 */
@FunctionalInterface
public interface RestFnProvider {
    RestFn get(RestFnConfig restFnConfig);
}
