package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpRequest;

import org.springframework.stereotype.Component;

import me.ehp246.aufrest.api.rest.ByRestListener;
import me.ehp246.aufrest.api.rest.RestRequest;

@Component
class ReqFilter implements ByRestListener {
	private HttpRequest httpReq;
	private RestRequest req;

	@Override
	public void onRequest(HttpRequest httpRequest, RestRequest req) {
		this.httpReq = httpRequest;
		this.req = req;
	}

	RestRequest reqByRest() {
		return this.req;
	}

	HttpRequest httpReq() {
		return this.httpReq;
	}
}
