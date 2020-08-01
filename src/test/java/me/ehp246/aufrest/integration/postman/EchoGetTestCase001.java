package me.ehp246.aufrest.integration.postman;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;

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
	 * Defaults to ResponseSupplier with String body.
	 *
	 * @return
	 */
	CompletableFuture<HttpResponse<?>> getAsResponseFuture();

}
