package me.ehp246.aufrest.integration.postman;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/post")
interface EchoPostTestCase001 {
	EchoStringData post(String str);

	EchoMapData post(Map<String, String> map);

	interface EchoResponseBody {
		Map<String, String> getArgs();

		Map<String, String> getHeaders();

		String getUrl();

	}
}
