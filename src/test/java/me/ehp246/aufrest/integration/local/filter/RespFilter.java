package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

import me.ehp246.aufrest.api.rest.ByRestListener;
import me.ehp246.aufrest.api.rest.RestRequest;

@Component
class RespFilter implements ByRestListener {
	private RestRequest req;
	private HttpResponse<?> resp;

	@Override
	public void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
		this.req = req;
		this.resp = httpResponse;
	}

	HttpResponse<?> httpResponse() {
		return resp;
	}

	RestRequest restRequest() {
		return this.req;
	}
}
