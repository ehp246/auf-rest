package me.ehp246.aufrest.integration.postman;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.RequestHeader;

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

	EchoResponseBody getAsEchoBody(@RequestHeader("x-auf-rest-id") String id);

	/**
	 * Defaults to ResponseSupplier with String body.
	 *
	 * @return
	 */
	CompletableFuture<HttpResponse<?>> getAsResponseFuture();

}
