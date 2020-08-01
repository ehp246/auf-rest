package org.ehp246.aufrest.integration;

import java.util.Map;

import org.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/delete")
public interface EchoDeleteTestCase001 {
	EchoMapData delete(Map<String, String> data);
}
