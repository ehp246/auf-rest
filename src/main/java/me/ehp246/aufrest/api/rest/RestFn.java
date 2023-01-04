package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface RestFn {
    HttpResponse<?> apply(RestRequest request, BodyDescriptor descriptor, ResponseConsumer consumer);

    default HttpResponse<?> apply(final RestRequest request) {
        return this.apply(request, request.body() == null ? null : new BodyDescriptor(request.body().getClass()),
                BodyHandlers::discarding);
    }

    public interface ResponseConsumer {
        BodyHandler<?> handler();
    }
}
