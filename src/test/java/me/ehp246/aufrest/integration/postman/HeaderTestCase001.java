package me.ehp246.aufrest.integration.postman;

import org.springframework.web.bind.annotation.RequestHeader;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/get")
public interface HeaderTestCase001 {
	EchoResponseBody get();

	EchoResponseBody get(@RequestHeader("X-aufrest-Trace-Id") String value);
}
