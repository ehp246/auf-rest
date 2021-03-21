package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpRequest;

import org.springframework.stereotype.Component;

import me.ehp246.aufrest.api.rest.RestRequest;

@Component
class ReqFilter {
	private HttpRequest httpReq;
	private RestRequest req;

	public HttpRequest apply(HttpRequest httpRequest, RestRequest req) {
		this.httpReq = httpRequest;
		this.req = req;
		return httpRequest;
	}

	RestRequest reqByRest() {
		return this.req;
	}

	HttpRequest httpReq() {
		return this.httpReq;
	}
}
