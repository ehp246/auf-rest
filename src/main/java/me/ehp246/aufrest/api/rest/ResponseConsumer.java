package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

import org.springframework.core.annotation.Order;

/**
 * Defines a Spring bean that AufRest invokes when a HTTP response is received
 * and successfully processed.
 * <p>
 * Multiple beans are supported and invoked by the {@link Order}.
 * 
 * @author Lei Yang
 * @since 2.2.2
 */
@FunctionalInterface
public interface ResponseConsumer {
	/**
	 * AufRest invokes this method when and only when a HTTP response is received.
	 * The response is passed in regardless the status code.
	 * <p>
	 * The method will not be invoked if there is an exception and no response.
	 * 
	 * @param httpResponse the HTTP response received
	 * @param req          the {@link RestRequest} that initiated the HTTP call
	 */
	void accept(HttpResponse<?> httpResponse, RestRequest req);
}
