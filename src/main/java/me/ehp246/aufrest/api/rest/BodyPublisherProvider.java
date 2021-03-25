/**
 * 
 */
package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest.BodyPublisher;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface BodyPublisherProvider {
	BodyPublisher get(RestRequest req);
}
