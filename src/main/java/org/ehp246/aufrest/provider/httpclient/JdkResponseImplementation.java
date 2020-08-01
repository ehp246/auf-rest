package org.ehp246.aufrest.provider.httpclient;

import java.net.http.HttpResponse;

import org.ehp246.aufrest.api.rest.Request;
import org.ehp246.aufrest.api.rest.Response;

/**
 * @author Lei Yang
 *
 */
final class JdkResponseImplementation implements Response {
	private final HttpResponse<?> httpResponse;
	private final Request req;

	/**
	 * @param req
	 * @param httpResponse
	 * @param httpRequest
	 */
	JdkResponseImplementation(final Request req, final HttpResponse<?> httpResponse) {
		this.httpResponse = httpResponse;
		this.req = req;
	}

	@Override
	public Request request() {
		return req;
	}

	@Override
	public HttpResponse<?> received() {
		return httpResponse;
	}
}