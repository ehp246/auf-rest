package me.ehp246.aufrest.core.byrest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * Test cases for RequestHeader support.
 *
 * defaultValue is not supported.
 *
 * Non-String type is supported by Object::toString.
 *
 * Null/empty/blank tolerant.
 *
 * @author Lei Yang
 *
 */
@ByRest("")
public interface RequestHeaderSpec001 {
	void get(@RequestHeader("x-correl-id") String correlId);

	/**
	 * Should be ignored.
	 *
	 * @param correlId
	 */
	void getBlank(@RequestHeader("") String correlId);

	/**
	 * Object::toString
	 *
	 * @param correlId
	 */
	void get(@RequestHeader("x-uuid") UUID correlId);

	/**
	 * Same Header repeated. Throw or override?
	 *
	 * @param correlId1
	 * @param correlId2
	 */
	void getRepeated(@RequestHeader("x-correl-id") String correlId1, @RequestHeader("x-correl-id") String correlId2);

	void getMultiple(@RequestHeader("x-span-id") String spanId, @RequestHeader("x-trace-id") String traceId);

	void get(@RequestHeader("accept-language") List<String> accepted);

	void get(@RequestHeader Map<String, String> headers);

	/**
	 * Explicitly named should take precedence.
	 *
	 * @param headers
	 * @param correlId
	 */
	void get(@RequestHeader Map<String, String> headers, @RequestHeader("x-correl-id") UUID correlId);

	void get(@RequestHeader MultiValueMap<String, String> headers);

	void getMapOfList(@RequestHeader Map<String, List<String>> headers);
}
