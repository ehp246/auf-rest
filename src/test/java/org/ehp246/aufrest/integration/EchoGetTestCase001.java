package org.ehp246.aufrest.integration;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.ehp246.aufrest.api.annotation.AsIs;
import org.ehp246.aufrest.api.annotation.ByRest;
import org.ehp246.aufrest.api.rest.Response;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/get")
interface EchoGetTestCase001 {
	/**
	 * Body defaults to String.
	 *
	 * @return
	 */
	Response getAsResponse();

	/**
	 * Body defaults to String.
	 *
	 * @return
	 */
	HttpResponse<String> getAsHttpResponse();

	InputStream getAsInputStream();

	/**
	 * Send the request and ignore the response
	 */
	void getVoid();

	Void getVoid2();

	EchoResponseBody getAsEchoBody();

	@AsIs
	String getBodyAsIs();

	/**
	 * Defaults to Response with String body.
	 *
	 * @return
	 */
	CompletableFuture<Response> getAsResponseFuture();

}
