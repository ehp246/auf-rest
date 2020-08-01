package org.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse;

/**
 * @author Lei Yang
 *
 */
public interface Response {
	Request request();

	HttpResponse<?> received();
}
