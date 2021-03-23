package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;

import org.springframework.core.annotation.Order;

/**
 * Defines a Spring bean that AufRest invokes before a HTTP request is sent.
 * <p>
 * Multiple beans are supported and invoked by the {@link Order}.
 * 
 * @author Lei Yang
 * @since 2.2.2
 */
@FunctionalInterface
public interface RequestConsumer {
	void accept(HttpRequest httpRequest, RestRequest req);
}
