package me.ehp246.aufrest.core.byrest.returntype;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.ParameterizedTypeReference;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.Reifying;

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

	@Reifying({ HttpResponse.class, List.class, Instant.class })
	CompletableFuture<HttpResponse<List<Instant>>> get003();

	// Should throw
	HttpResponse get004();

	// Should throw
	HttpResponse<EchoResponseBody> get005();

	List<Instant> get006(ParameterizedTypeReference<List<Instant>> typeRef);

	interface EchoResponseBody {
		Map<String, String> getArgs();

		Map<String, String> getHeaders();

		String getUrl();

		String getData();

		String getJson();
	}
}
