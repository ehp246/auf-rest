package me.ehp246.aufrest.api.rest;

/**
 * The abstraction of an object that can consume a text response and turn it
 * into an Java object of specified type.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface TextContentConsumer {
	Object consume(String text, Receiver receiver);
}
