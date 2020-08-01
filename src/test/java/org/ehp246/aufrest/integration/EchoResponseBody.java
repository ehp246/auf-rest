package org.ehp246.aufrest.integration;

import java.util.Map;

public interface EchoResponseBody {
	Map<String, String> getArgs();

	Map<String, String> getHeaders();

	String getUrl();

	String getData();

	String getJson();
}