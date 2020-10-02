package me.ehp246.aufrest.integration.postman.header;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.integration.postman.EchoResponseBody;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/get")
interface HeaderTestCase001 {
	EchoResponseBody get();

	EchoResponseBody get(@RequestHeader("X-aufrest-Trace-Id") String value);

	EchoResponseBody get(@RequestHeader Map<String, List<String>> headers);
}
