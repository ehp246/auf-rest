package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface ClientFn {
	HttpResponse<?> apply(Request request);
}
