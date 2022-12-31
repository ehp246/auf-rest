package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface RestFn {
    HttpResponse<?> apply(RestRequest request, ResponseConsumer consumer);

    public interface ResponseConsumer {
        BodyHandler<?> handler();
    }
}
