package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.core.annotation.Order;

/**
 * Defines a Spring bean that can receive request/response events. Multiple
 * beans are supported and to be invoked in {@link Order}.
 * <p>
 * Auf REST does not suppress any exception on the beans.
 * 
 * @author Lei Yang
 */
public interface RestListener {
    /**
     * Invoked just before the request is sent.
     * <p>
     * The default implementation does nothing.
     * 
     * @param httpRequest the HTTP request to be sent
     * @param req         the {@link RestRequest} that initiated the HTTP request
     */
    default void onRequest(HttpRequest httpRequest, RestRequest req) {
    }

    /**
     * Invoked when and only when a HTTP response is received. The status code
     * doesn't matter.
     * <p>
     * Only applies when there is a response.
     * <p>
     * The default implementation does nothing.
     * 
     * @param httpResponse the HTTP response received
     * @param req          the {@link RestRequest} that initiated the HTTP call
     */
    default void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
    }

    /**
     * Invoked when and only when a checked exception is raised while sending the
     * request. The listeners are invoked right before the exception is thrown up
     * the call stack.
     * <p>
     * Only applies when there is a exception.
     * <p>
     * The default implementation does nothing.
     * 
     * @param exception
     * @param httpRequest
     * @param req
     */
    default void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
    }
}
