package me.ehp246.aufrest.integration.postman;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/put")
public interface EchoPutTestCase001 {
	EchoMapData put(Map<String, String> data);
}
