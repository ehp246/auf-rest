package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import me.ehp246.aufrest.api.rest.BodyDescriptor.JsonViewValue;

/**
 * The abstraction of a HttpClient that takes in a request and returns a
 * response synchronously.
 *
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface RestFn {
    HttpResponse<?> apply(RestRequest request, BodyDescriptor descriptor, ResponseHandler consumer);

    /**
     * Sends the request body as a simple object reference in JSON.
     * <p>
     * Returns the response body as a {@linkplain Map}.
     *
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    default HttpResponse<Map<String, Object>> apply(final RestRequest request) {
        return (HttpResponse<Map<String, Object>>) this.apply(request,
                request.body() == null ? null : new JsonViewValue(request.body().getClass()),
                BodyHandlers::discarding);
    }

    public interface ResponseHandler {
        BodyHandler<?> handler();
    }
}
