package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Type;

/**
 * @author Lei Yang
 *
 */
interface AuthTestCases {
	@ByRest("")
	interface Case001 {
		void get();
	}

	@ByRest(value = "", auth = @Auth(value = "postman:password", type = Type.BASIC))
	interface Case002 {
		void get();
	}

	@ByRest(value = "", auth = @Auth("${api.bearer.token}"))
	interface Case003 {
		void get();
	}

	@ByRest(value = "", auth = @Auth(value = "CustomKey custom.header.123", type = Type.ASIS))
	interface Case004 {
		void get();
	}

	@ByRest(value = "", auth = @Auth(value = "${postman.username}:${postman.password}", type = Type.BASIC))
	interface Case005 {
		void get();
	}
}
