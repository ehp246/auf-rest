package me.ehp246.aufrest.integration.local.filter;

import org.springframework.stereotype.Component;

import me.ehp246.aufrest.api.rest.RestResponse;

@Component
class RespFilter {
	private RestResponse restResp;

	RestResponse apply(RestResponse resp) {
		this.restResp = resp;
		return resp;
	}

	RestResponse responseByRest() {
		return restResp;
	}

}
