package me.ehp246.aufrest.api.rest;

/**
 * The abstraction of an object that can consume a text response and turn it
 * into an Java object of specified type.
 *
 * @author Lei Yang
 */
public interface BodyConsumer {
	Object consume(Object body, Receiver receiver);
}
