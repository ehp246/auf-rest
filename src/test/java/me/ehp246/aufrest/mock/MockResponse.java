package me.ehp246.aufrest.mock;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestResponse;

public class MockResponse implements RestResponse {
	private final RestRequest req;
	private final HttpResponse<Object> resp;

	public MockResponse(final RestRequest req, final HttpResponse<Object> resp) {
		super();
		this.req = req;
		this.resp = resp;
	}

	public MockResponse(RestRequest req) {
		this(req, new MockHttpResponse<>());
	}

	@Override
	public RestRequest restRequest() {
		return req;
	}

	@Override
	public HttpResponse<Object> httpResponse() {
		return resp;
	}

	@Override
	public HttpRequest httpRequest() {
		return resp.request();
	}

}
