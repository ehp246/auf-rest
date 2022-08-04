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
interface HeaderTestCases {
    @ByRest(value = "", acceptGZip = false)
    interface AcceptGZipCase01 {
        void get();
    }

    @ByRest("")
    interface HeaderCase01 {

        void get(@RequestHeader("x-correl-id") String correlId);

        void getBlank(@RequestHeader("") String correlId);

        /**
         * Object::toString
         */
        void get(@RequestHeader("x-uuid") UUID correlId);

        void getRepeated(@RequestHeader("x-correl-id") String correlId1,
                @RequestHeader("x-correl-Id") String correlId2);

        void getMultiple(@RequestHeader("x-span-id") String spanId, @RequestHeader("x-trace-id") String traceId);

        void get(@RequestHeader("accept-language") List<String> accepted);

        void get(@RequestHeader Map<String, String> headers);

        void get(@RequestHeader Map<String, String> headers, @RequestHeader("x-correl-id") String correlId);

        void get(@RequestHeader MultiValueMap<String, String> headers);

        void getMapOfList(@RequestHeader Map<String, List<String>> headers);

        void getListOfList(@RequestHeader("accept-language") List<List<String>> accepted);
    }

    @ByRest(value = "", headers = { "x-api-key", "api.key" })
    interface HeaderCase02 {
        void get();

        void get(@RequestHeader("x-api-key") String key);

        void getCasing(@RequestHeader("x-API-key") String key);
    }

    @ByRest(value = "", headers = { "x-api-key", "${api.key}" })
    interface HeaderCase03 {
        void get();
    }

    @ByRest(value = "", headers = { "x-api-key-1", "api.key.1", "x-api-key-2", "api.key.2" })
    interface HeaderCase04 {
        void get();
    }

    @ByRest(value = "", headers = { "x-api-key-1", "api.key.1", "x-api-key-2" })
    interface HeaderCase05 {
        void get();
    }

    @ByRest(value = "", headers = { "x-api-key-1", "api.key.1", "x-api-key-1", "api.key.1" })
    interface HeaderCase06 {
        void get();
    }

    @ByRest(value = "", headers = { "Authorization", "api.key" })
    interface HeaderCase07 {
        void get();
    }
}
