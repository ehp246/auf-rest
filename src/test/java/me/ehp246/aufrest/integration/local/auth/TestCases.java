package me.ehp246.aufrest.integration.local.auth;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme;
import me.ehp246.aufrest.api.annotation.Reifying;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
	@ByRest("http://localhost:${local.server.port}/auth/basic")
	interface BasicCase001 {
		void get();

		void get(@AuthHeader String basic);

		@Reifying(Void.class)
		HttpResponse<Void> getAsResponse();
	}

	@ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(args = { "basicuser",
			"password" }, scheme = Scheme.BASIC))
	interface BasicCase002 {
		void get();

		void get(@AuthHeader String basic);
	}

	@ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(args = "basicuser:password", scheme = Scheme.BEARER))
	interface BasicCase003 {
		void get();

		void get(@AuthHeader String basic);
	}

	@ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(args = "Basic YmFzaWN1c2VyOnBhc3N3b3Jk", scheme = Scheme.SIMPLE))
	interface BasicCase004 {
		void get();

		void get(@AuthHeader String basic);
	}
}
