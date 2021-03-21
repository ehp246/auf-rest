package me.ehp246.aufrest.mock;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.rest.RequestByRest;
import me.ehp246.aufrest.api.rest.ResponseByRest;

public class MockResponse implements ResponseByRest {
	private final RequestByRest req;
	private final HttpResponse<Object> resp;

	public MockResponse(final RequestByRest req, final HttpResponse<Object> resp) {
		super();
		this.req = req;
		this.resp = resp;
	}

	public MockResponse(RequestByRest req) {
		this(req, new MockHttpResponse<>());
	}

	@Override
	public RequestByRest requestByRest() {
		return req;
	}

	@Override
	public HttpResponse<Object> httpResponse() {
		return resp;
	}

}
