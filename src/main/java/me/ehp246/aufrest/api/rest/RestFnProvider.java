package me.ehp246.aufrest.api.rest;

/**
 * An object that can provide a {@link RestFn} according to
 * {@link ClientConfig}.
 * 
 * @author Lei Yang
 */
@FunctionalInterface
public interface RestFnProvider {
	RestFn get(ClientConfig clientConfig);
}
