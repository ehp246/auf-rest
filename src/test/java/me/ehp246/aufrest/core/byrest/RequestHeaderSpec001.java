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
 * Non-String type is supported by <code>Object.toString()</code>.
 *
 * Empty/blank strings are tolerated and added as is.
 *
 * <code>null</code>'s are filtered out.
 *
 * @author Lei Yang
 *
 */
@ByRest("")
interface RequestHeaderSpec001 {
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
     * Same Header repeated are concatenated
     *
     * @param correlId1
     * @param correlId2
     */
    void getRepeated(@RequestHeader("x-correl-id") String correlId1, @RequestHeader("x-correl-id") String correlId2);

    void getMultiple(@RequestHeader("x-span-id") String spanId, @RequestHeader("x-trace-id") String traceId);

    void get(@RequestHeader("accept-language") List<String> accepted);

    void get(@RequestHeader Map<String, String> headers);

    void get(@RequestHeader Map<String, String> headers, @RequestHeader("x-correl-id") String correlId);

    void get(@RequestHeader MultiValueMap<String, String> headers);

    void getMapOfList(@RequestHeader Map<String, List<String>> headers);

    void getListOfList(@RequestHeader("accept-language") List<List<String>> accepted);
}
