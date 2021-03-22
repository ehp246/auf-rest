package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

/**
 * @author Lei Yang
 * @since 2.2.2
 */
@FunctionalInterface
public interface RequestConsumer {
	void accept(HttpRequest httpRequest, RestRequest req);
}
