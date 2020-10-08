package me.ehp246.aufrest.integration.postman.method;

import org.springframework.web.bind.annotation.RequestHeader;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.integration.postman.EchoResponseBody;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/get")
interface EchoGetTestCase001 {
	/**
	 * Send the request and ignore the response
	 */
	void getVoid();

	Void getVoid2();

	EchoResponseBody getAsEchoBody();

	EchoResponseBody getAsEchoBody(@RequestHeader("x-auf-rest-id") String id);

}
