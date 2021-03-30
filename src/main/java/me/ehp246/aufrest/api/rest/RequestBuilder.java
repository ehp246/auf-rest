package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface RequestBuilder {
	HttpRequest apply(RestRequest req);
}
