package me.ehp246.aufrest.core.byrest;

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
@SuppressWarnings("rawtypes")
@ByRest("")
interface ReturnTypeTestCase001 {
	CompletableFuture get001();

	@Reifying(HttpResponse.class)
	CompletableFuture<HttpResponse> get002();

	// TODO:
	@Reifying({ HttpResponse.class, List.class })
	CompletableFuture<HttpResponse<List>> get003();

	// Should throw
	HttpResponse get004();

	// Should throw
	HttpResponse<EchoResponseBody> get005();
}
