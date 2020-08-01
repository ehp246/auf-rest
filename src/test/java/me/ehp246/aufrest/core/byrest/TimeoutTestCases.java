package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
interface TimeoutTestCases {
	@ByRest("")
	interface Case001 {
		void get();
	}

	@ByRest(value = "", timeout = -1)
	interface Case002 {
		void get();
	}

	@ByRest(value = "", timeout = 11)
	interface Case003 {
		void get();
	}
}
