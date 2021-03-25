/**
 * 
 */
package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.core.annotation.Order;

/**
 * Defines a Spring bean that can receive request/response events. Multiple
 * beans are supported and to be invoked in {@link Order}.
 * <p>
 * AufRest does not suppress any exception on the beans.
 * 
 * @author Lei Yang
 */
public interface RestConsumer {
	/**
	 * AufRest invokes this method just before the request is sent.
	 * 
	 * @param httpRequest the HTTP request to be sent
	 * @param req         the {@link RestRequest} that initiated the HTTP request
	 */
	default void preSend(HttpRequest httpRequest, RestRequest req) {
	}

	/**
	 * AufRest invokes this method when and only when a HTTP response is received.
	 * The response is passed in regardless the status code.
	 * <p>
	 * The method will not be invoked if there is an exception and no response.
	 * 
	 * @param httpResponse the HTTP response received
	 * @param req          the {@link RestRequest} that initiated the HTTP call
	 */
	default void postSend(HttpResponse<?> httpResponse, RestRequest req) {
	}

	/**
	 * AufRest invokes this method when and only when an Exception is raised while
	 * sending the request. The consumers are invoked right before the exception is
	 * thrown up the call stack. Consumers cann't stop the propagation.
	 * <p>
	 * The method will not be invoked if there is no exception and a response is
	 * received regardless the status code.
	 * 
	 * @param exception
	 * @param httpRequest
	 * @param req
	 */
	default void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
	}
}
