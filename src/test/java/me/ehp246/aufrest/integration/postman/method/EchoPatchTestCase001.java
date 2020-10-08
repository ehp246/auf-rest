package me.ehp246.aufrest.integration.postman.method;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/patch")
interface EchoPatchTestCase001 {
	EchoMapData patch(Map<String, String> data);
}
