package me.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.rest.Request;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface BodyHandlerProvider {
	BodyHandler<?> get(Request request);
}
