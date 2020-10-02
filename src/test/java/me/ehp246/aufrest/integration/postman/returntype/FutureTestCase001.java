package me.ehp246.aufrest.integration.postman.returntype;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.integration.postman.EchoResponseBody;

/**
 * @author Lei Yang
 *
 */
@ByRest("https://postman-echo.com/get")
interface FutureTestCase001 {
	@Reifying({ EchoResponseBody.class })
	CompletableFuture<EchoResponseBody> get001();

	@Reifying({ HttpResponse.class, EchoResponseBody.class })
	CompletableFuture<HttpResponse<EchoResponseBody>> get002();

	// TODO
	@Reifying({ List.class, EchoResponseBody.class })
	CompletableFuture<List<EchoResponseBody>> getAsFuture_004();

	// TODO
	@Reifying({ HttpResponse.class, List.class, EchoResponseBody.class })
	CompletableFuture<HttpResponse<List<EchoResponseBody>>> getAsFuture_003();

	// TODO
	@Reifying({ EchoResponseBody.class })
	List<EchoResponseBody> getAsList();
}
