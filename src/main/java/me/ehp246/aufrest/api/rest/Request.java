package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;

/**
 * The abstraction of a Rest request message.
 *
 * @author Lei Yang
 *
 */
public interface Request extends Messsage {
	String uri();

	default String method() {
		return "GET";
	}

	default BodyHandler<?> bodyHandler() {
		return HttpResponse.BodyHandlers.ofString();
	}

	default Duration timeout() {
		return null;
	}

	default String authentication() {
		return null;
	}
}
