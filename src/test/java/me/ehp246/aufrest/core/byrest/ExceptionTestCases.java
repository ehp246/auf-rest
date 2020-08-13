package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * @author Lei Yang
 *
 */
interface ExceptionTestCases {
	@ByRest("")
	interface Case001 {
		void get(HttpResponse<String> response);

		void getWithThrows(HttpResponse<String> response) throws UnhandledResponseException;
	}
}
