package org.ehp246.aufrest.integration;

import java.util.Map;

import org.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/patch")
interface EchoPatchTestCase001 {
	EchoMapData patch(Map<String, String> data);
}
