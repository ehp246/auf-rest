package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 
 * @author Lei Yang
 * @since 2.2
 */
public interface ResponseByRest {
	RequestByRest requestByRest();

	HttpResponse<?> httpResponse();

	default HttpRequest httpRequest() {
		return this.httpResponse().request();
	}
}
