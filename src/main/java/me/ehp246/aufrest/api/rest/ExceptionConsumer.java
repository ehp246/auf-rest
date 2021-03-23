package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 * @since 2.2.2
 */
@FunctionalInterface
public interface ExceptionConsumer {
	void accept(Exception ex, RestRequest req);
}
