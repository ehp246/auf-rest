package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

/**
 * @author Lei Yang
 * @since 2.1
 * @version 2.1.1
 */
@FunctionalInterface
public interface RequestFilter {
	HttpRequest apply(HttpRequest httpRequest, ReqByRest req);
}
