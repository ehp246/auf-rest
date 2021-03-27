package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme;

/**
 * @author Lei Yang
 *
 */
interface AuthTestCases {
	@ByRest("")
	interface Case001 {
		void get();

		void get(@AuthHeader String auth);
	}

	@ByRest(value = "", auth = @Auth(args = { "postman", "password" }, scheme = Scheme.BASIC))
	interface Case002 {
		void get();

		void get(@AuthHeader String auth);
	}

	@ByRest(value = "", auth = @Auth(args = "${api.bearer.token}"))
	interface Case003 {
		void get();

		void get(@AuthHeader String auth);
	}

	@ByRest(value = "", auth = @Auth(args = "CustomKey custom.header.123", scheme = Scheme.SIMPLE))
	interface Case004 {
		void get();

		void get(@AuthHeader String auth);
	}

	@ByRest(value = "", auth = @Auth(args = { "${postman.username}", "${postman.password}" }, scheme = Scheme.BASIC))
	interface Case005 {
		void get();

		void get(@AuthHeader String auth);
	}

	// Accepted
	@ByRest(value = "", auth = @Auth(args = { " ", " " }, scheme = Scheme.BASIC))
	interface Case006 {
		void get();

		// With Auth if not blank
		void get(@AuthHeader String auth);
	}

	// Exception
	@ByRest(value = "", auth = @Auth(scheme = Scheme.BASIC))
	interface Case007 {
		void get();

		// With Auth if not blank
		void get(@AuthHeader String auth);
	}

	// Exception
	@ByRest(value = "", auth = @Auth(scheme = Scheme.BEARER))
	interface Case008 {
		void get();

		// With Auth if not blank
		void get(@AuthHeader String auth);
	}

	// Exception
	@ByRest(value = "", auth = @Auth(scheme = Scheme.SIMPLE))
	interface Case009 {
		void get();

		// With Auth if not blank
		void get(@AuthHeader String auth);
	}

	@ByRest(value = "", auth = @Auth(scheme = Scheme.NONE))
	interface Case010 {
		// AuthSupplier returns null.
		void get();

		// With Auth if not blank
		void get(@AuthHeader String auth);
	}
}
