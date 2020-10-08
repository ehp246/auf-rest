package me.ehp246.aufrest.integration.postman.method;

import java.util.Map;

import me.ehp246.aufrest.integration.postman.method.EchoPostTestCase001.EchoResponseBody;

public interface EchoMapData extends EchoResponseBody {
	Map<String, String> getJson();
}