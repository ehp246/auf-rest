package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * @author Lei Yang
 */
@FunctionalInterface
public interface ResponseFilter {
	HttpResponse<?> apply(HttpResponse<?> httpResponse, RestRequest req);
}
