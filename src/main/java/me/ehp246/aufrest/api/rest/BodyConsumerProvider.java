package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 * @since 2.0.1
 */
@FunctionalInterface
public interface BodyConsumerProvider {
	BodyConsumer get(String mediaType);
}
