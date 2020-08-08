package me.ehp246.aufrest.api.rest;

/**
 * Defines a global bean that provides a producer according to the media type
 * for the request body.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ContentProducerProvider {
	TextContentProducer get(String mediaType);
}
