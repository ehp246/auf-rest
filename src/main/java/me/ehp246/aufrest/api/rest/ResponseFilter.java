package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * @author Lei Yang
 * @since 2.1
 * @version 2.1.1
 */
@FunctionalInterface
public interface ResponseFilter {
	HttpResponse<?> apply(HttpResponse<?> httpRespons, RequestByRest req); 
}
