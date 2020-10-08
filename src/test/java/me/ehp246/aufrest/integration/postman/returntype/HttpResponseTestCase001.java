package me.ehp246.aufrest.integration.postman.returntype;

import java.net.http.HttpResponse;
import java.util.List;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.integration.postman.EchoResponseBody;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("https://postman-echo.com/get")
interface HttpResponseTestCase001 {

	// Body type goes with the annotation.
	@Reifying(EchoResponseBody.class)
	HttpResponse<EchoResponseBody> get003();

	@Reifying(EchoResponseBody.class)
	HttpResponse get004();

	/**
	 * No way to validate at invocation. Real type goes with the annotation. Cast
	 * exception will happen on read.
	 */
	@Reifying(EchoResponseBody.class)
	HttpResponse<String> get005();

	@AsIs
	@Reifying(String.class)
	HttpResponse<String> get006();

	// TODO:
	@Reifying({ List.class, EchoResponseBody.class })
	HttpResponse<List<EchoResponseBody>> get007();
}
