package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * @author Lei Yang
 * @since 2.2.2
 */
@FunctionalInterface
public interface ResponseConsumer {
	void accept(HttpResponse<?> httpResponse, RestRequest req);
}
