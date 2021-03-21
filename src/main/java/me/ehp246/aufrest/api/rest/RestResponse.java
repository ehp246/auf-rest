package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 
 * @author Lei Yang
 * @since 2.2
 */
public interface RestResponse {
	RestRequest restRequest();

	/**
	 * The initiating HTTP request created from the {@link RestRequest} before sent.
	 * It could be different from the request returned by the HTTP response.
	 * 
	 * @return
	 */
	HttpRequest httpRequest();

	HttpResponse<?> httpResponse();
}
