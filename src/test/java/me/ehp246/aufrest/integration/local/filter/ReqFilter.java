package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpRequest;

import org.springframework.stereotype.Component;

import me.ehp246.aufrest.api.rest.RestObserver;
import me.ehp246.aufrest.api.rest.RestRequest;

@Component
class ReqFilter implements RestObserver {
	private HttpRequest httpReq;
	private RestRequest req;

	@Override
	public void preSend(HttpRequest httpRequest, RestRequest req) {
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
