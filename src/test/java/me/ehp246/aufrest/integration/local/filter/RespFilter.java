package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

import me.ehp246.aufrest.api.rest.RestObserver;
import me.ehp246.aufrest.api.rest.RestRequest;

@Component
class RespFilter implements RestObserver {
	private RestRequest req;
	private HttpResponse<?> resp;

	@Override
	public void postSend(HttpResponse<?> httpResponse, RestRequest req) {
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
