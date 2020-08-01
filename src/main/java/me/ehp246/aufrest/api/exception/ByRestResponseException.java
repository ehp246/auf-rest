package me.ehp246.aufrest.api.exception;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.Request;

/**
 * @author Lei Yang
 *
 */
public class ByRestResponseException extends RuntimeException {
	private static final long serialVersionUID = 3813318541456042414L;

	private final Request request;
	private final HttpResponse<?> response;

	public ByRestResponseException(final Request request, final HttpResponse<?> response) {
		super();
		this.request = request;
		this.response = response;
	}

	public HttpResponse<?> httpResponse() {
		return this.response;
	}

	public int statusCode() {
		return this.httpResponse().statusCode();
	}

	public String bodyAsString() {
		return this.httpResponse().body().toString();
	}

	public Request request() {
		return request;
	}
}
