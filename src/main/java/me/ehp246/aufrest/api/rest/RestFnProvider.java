package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface RestFnProvider {
	RestFn get(ClientConfig clientConfig);
}
