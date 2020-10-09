package me.ehp246.aufrest.integration.postman.auth;

import java.net.http.HttpResponse;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Type;
import me.ehp246.aufrest.api.annotation.Reifying;

/**
 * @author Lei Yang
 *
 */
interface EchoAuthTestCases {
	@ByRest("${echo.base}/basic-auth")
	interface BasicCase001 {
		Map<String, Boolean> get();

		@Reifying(String.class)
		@AsIs
		HttpResponse<String> getAsResponse();
	}

	@ByRest(value = "${echo.base}/basic-auth", auth = @Auth(value = "postman:password", type = Type.BASIC))
	interface BasicCase002 {
		Map<String, Boolean> get();
	}

	@ByRest(value = "${echo.base}/basic-auth", auth = @Auth(value = "postman:password", type = Type.BEARER))
	interface BasicCase003 {
		Map<String, Boolean> get();
	}

	@ByRest(value = "${echo.base}/basic-auth", auth = @Auth(value = "Basic cG9zdG1hbjpwYXNzd29yZA==", type = Type.ASIS))
	interface BasicCase004 {
		Map<String, Boolean> get();
	}
}
