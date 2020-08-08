package me.ehp246.aufrest.api.rest;

import java.time.Duration;

import me.ehp246.aufrest.api.rest.TextContentConsumer.Receiver;

/**
 * The abstraction of a Rest request message with an expected response.
 *
 * @author Lei Yang
 *
 */
public interface Request extends Messsage {
	String uri();

	default String method() {
		return "GET";
	}

	default Duration timeout() {
		return null;
	}

	default String authentication() {
		return null;
	}

	default String contentType() {
		return MediaType.APPLICATION_JSON;
	}

	default String accept() {
		return MediaType.APPLICATION_JSON;
	}

	default Receiver receiver() {
		return () -> void.class;
	}
}
