package me.ehp246.aufrest.integration.postman;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.RequestHeader;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/get")
public interface HeaderTestCase001 {
	EchoResponseBody get();

	CompletableFuture<HttpResponse<String>> getAsFuture();

	EchoResponseBody get(@RequestHeader("X-aufrest-Trace-Id") String value);

	EchoResponseBody get(@RequestHeader Map<String, List<String>> headers);
}
