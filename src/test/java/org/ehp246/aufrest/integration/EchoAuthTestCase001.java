package org.ehp246.aufrest.integration;

import java.util.Map;

import org.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/basic-auth")
interface EchoAuthTestCase001 {
	Map<String, Boolean> get();
}
