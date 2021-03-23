/**
 * 
 */
package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpRequest;

import me.ehp246.aufrest.api.rest.RequestConsumer;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
class ReqConsumer implements RequestConsumer {
	private final int id;

	private HttpRequest httpReq;
	private RestRequest req;

	public ReqConsumer(int id) {
		super();
		this.id = id;
	}

	@Override
	public void accept(HttpRequest httpRequest, RestRequest req) {
		this.httpReq = httpRequest;
		this.req = req;
	}

	RestRequest reqByRest() {
		return this.req;
	}

	HttpRequest httpReq() {
		return this.httpReq;
	}

	int id() {
		return this.id;
	}
}
