package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.core.annotation.Order;

import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;

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
     * @param restRequest         the {@link RestRequest} that initiated the HTTP request
     */
    default void onRequest(final HttpRequest httpRequest, final RestRequest restRequest) {
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
     * @param restRequest          the {@link RestRequest} that initiated the HTTP call
     */
    default void onResponse(final HttpResponse<?> httpResponse, final RestRequest restRequest) {
    }

    /**
     * Invoked when a checked exception is raised while sending a request and
     * receiving the response.
     * <p>
     * The listeners are invoked before the exception is thrown up the call stack.
     * <p>
     * The listeners are not invoked on an {@linkplain UnhandledResponseException}.
     * <p>
     * If an exception is thrown by a listener, the new exception will be added to
     * the original as a {@linkplain Exception#addSuppressed(Throwable) suppressed}.
     * In this case, the rest listeners will be skipped.
     * <p>
     * The default implementation does nothing.
     *
     * @param exception
     * @param httpRequest
     * @param restRequest
     * @see HttpClient#send(HttpRequest, java.net.http.HttpResponse.BodyHandler)
     */
    default void onException(final Exception exception, final HttpRequest httpRequest, final RestRequest restRequest) {
    }
}
