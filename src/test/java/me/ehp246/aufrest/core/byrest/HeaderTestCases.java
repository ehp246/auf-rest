package me.ehp246.aufrest.core.byrest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.MultiValueMap;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;

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

        void get(@OfHeader("x-correl-id") String correlId);

        void getBlank(@OfHeader("") String correlId);

        /**
         * Object::toString
         */
        void get(@OfHeader("x-uuid") UUID correlId);

        void getRepeated(@OfHeader("x-correl-id") String correlId1,
                @OfHeader("x-correl-Id") String correlId2);

        void getMultiple(@OfHeader("x-span-id") String spanId, @OfHeader("x-trace-id") String traceId);

        void get(@OfHeader("accept-language") List<String> accepted);

        void get(@OfHeader Map<String, String> headers);

        void get(@OfHeader Map<String, String> headers, @OfHeader("x-correl-id") String correlId);

        void get(@OfHeader MultiValueMap<String, String> headers);

        void getMapOfList(@OfHeader Map<String, List<String>> headers);

        void getListOfList(@OfHeader("accept-language") List<List<String>> accepted);
    }

    @ByRest(value = "", headers = { "x-api-key", "api.key" })
    interface HeaderCase02 {
        void get();

        void get(@OfHeader("x-api-key") String key);

        void getCasing(@OfHeader("x-API-key") String key);
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
