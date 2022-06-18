package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 * 
 */
@FunctionalInterface
public interface HttpFn {
    HttpResponse<?> apply(RestRequest request);
}
