package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.core.annotation.Order;

import me.ehp246.aufrest.api.exception.ErrorResponseException;

/**
 * Defines a Spring bean that can receive request/response events. Multiple
 * beans are supported and to be invoked in {@link Order}.
 * <p>
 * Auf REST does not suppress any exception from the beans.
 *
 * @author Lei Yang
 * @since 1.0
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
    default void onRequest(final HttpRequest httpRequest, final RestRequest req) {
    }

    /**
     * Invoked as soon as a {@linkplain HttpResponse} is received for a request. The
     * status code doesn't matter.
     * <p>
     * Only applies when there is a response.
     * <p>
     * {@linkplain ErrorResponseException} propagation has no impact.
     * <p>
     * The default implementation does nothing.
     *
     * @param httpResponse the HTTP response received
     * @param req          the {@link RestRequest} that initiated the HTTP call
     */
    default void onResponse(final HttpResponse<?> httpResponse, final RestRequest req) {
    }

    /**
     * Invoked when and only when a checked exception is raised while sending a
     * request from a {@linkplain HttpClient}. The listeners are invoked right
     * before the exception is thrown up the call stack.
     * <p>
     * Only applies when there is a checked exception from
     * {@linkplain HttpClient#send(HttpRequest, java.net.http.HttpResponse.BodyHandler)}.
     * <p>
     * The default implementation does nothing.
     *
     * @param exception
     * @param httpRequest
     * @param req
     * @see HttpClient#send(HttpRequest, java.net.http.HttpResponse.BodyHandler)
     */
    default void onException(final Exception exception, final HttpRequest httpRequest, final RestRequest req) {
    }
}
