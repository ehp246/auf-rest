package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 *
 */
public interface JsonProvider {
	String toJson(Object value);

	Object fromJson(final String json, final Receiver receiver);
}
