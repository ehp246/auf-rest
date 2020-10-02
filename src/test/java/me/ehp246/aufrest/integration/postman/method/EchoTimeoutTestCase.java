package me.ehp246.aufrest.integration.postman.method;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "${echo.base}/get", timeout = 1)
interface EchoTimeoutTestCase {
	String get();
}
