package org.ehp246.aufrest.integration;

import java.util.Map;

import org.ehp246.aufrest.integration.EchoPostTestCase001.EchoResponseBody;

public interface EchoMapData extends EchoResponseBody {
	Map<String, String> getJson();
}