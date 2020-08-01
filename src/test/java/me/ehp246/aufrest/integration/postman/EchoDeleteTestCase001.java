package me.ehp246.aufrest.integration.postman;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/delete")
public interface EchoDeleteTestCase001 {
	EchoMapData delete(Map<String, String> data);
}
