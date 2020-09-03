package me.ehp246.aufrest.api.rest;

import java.time.Duration;
import java.util.function.Supplier;

import me.ehp246.aufrest.api.rest.TextContentConsumer.Receiver;

/**
 * The abstraction of a REST request message that expects a response.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface Request extends Messsage {
	String uri();

	default String method() {
		return "GET";
	}

	default Duration timeout() {
		return null;
	}

	default Supplier<String> authSupplier() {
		return null;
	}

	default String contentType() {
		return HttpUtils.APPLICATION_JSON;
	}

	default String accept() {
		return HttpUtils.APPLICATION_JSON;
	}

	default Receiver receiver() {
		return () -> void.class;
	}
}
