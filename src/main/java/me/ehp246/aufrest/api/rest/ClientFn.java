package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ClientFn {
	HttpResponse<?> apply(Request request);
}
