package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 * @since 1.0
 * @version 2.1
 */
@FunctionalInterface
public interface ClientFnProvider {
	ClientFn get(ClientConfig clientConfig);
}
