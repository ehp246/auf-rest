package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;

/**
 * The abstraction of a HTTP message. It can be a request or a response.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface Message {
	default Object body() {
		return null;
	}

	default Map<String, List<String>> headers() {
		return null;
	}
}
