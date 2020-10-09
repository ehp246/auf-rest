package me.ehp246.aufrest.integration.local.auth;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Type;
import me.ehp246.aufrest.api.annotation.Reifying;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
	@ByRest("http://localhost:${local.server.port}/auth/basic")
	interface BasicCase001 {
		void get();

		@Reifying(Void.class)
		HttpResponse<Void> getAsResponse();
	}

	@ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "basicuser:password", type = Type.BASIC))
	interface BasicCase002 {
		void get();
	}

	@ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "basicuser:password", type = Type.BEARER))
	interface BasicCase003 {
		void get();
	}

	@ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "Basic YmFzaWN1c2VyOnBhc3N3b3Jk", type = Type.ASIS))
	interface BasicCase004 {
		void get();
	}
}
