package me.ehp246.aufrest.api.rest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * HTTP-related constants.
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class HttpUtils {
    // Methods
    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String PUT = "PUT";
    public final static String PATCH = "PATCH";
    public final static String DELETE = "DELETE";
    public final static Set<String> METHOD_NAMES = Set.of(GET, POST, PUT, PATCH, DELETE);

    // Headers
    public final static String AUTHORIZATION = "authorization";
    public final static String CONTENT_TYPE = "content-type";
    public final static String CONTENT_LENGTH = "content-length";
    public static final String CONTENT_ENCODING = "content-encoding";
    public final static String ACCEPT = "accept";
    public static final String ACCEPT_ENCODING = "accept-encoding";
    public static final String TRACEPARENT = "traceparent";

    public final static String BEARER = "Bearer";
    public final static String BASIC = "Basic";

    public final static Set<String> RESERVED_HEADERS = Set.of(AUTHORIZATION, CONTENT_TYPE, ACCEPT, ACCEPT_ENCODING);

    // Media types
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String OCTET_STREAM = "application/octet-stream";

    private HttpUtils() {
        super();
    }

    /**
     * Encodes a single URL path element.
     *
     */
    public static String encodeUrlPath(final String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8).replaceAll("\\+", "%20").replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
    }

    public static String toQueryString(final Map<String, List<String>> queries) {
        if (queries == null || queries.size() == 0) {
            return "";
        }

        final var strBuf = new StringBuffer();
        for (final var entry : queries.entrySet()) {
            final var name = entry.getKey();
            final var values = entry.getValue();
            if (values == null || values.size() == 0) {
                continue;
            }

            final var encoded = values.stream().map(value -> URLEncoder.encode(value, StandardCharsets.UTF_8))
                    .collect(Collectors.toList());

            if (!strBuf.isEmpty()) {
                strBuf.append("&");
            }
            strBuf.append(name + "=");
            strBuf.append(String.join(",", encoded));
        }

        return strBuf.toString();
    }

    public static boolean isSuccess(final int code) {
        return code >= 200 && code < 300;
    }
}
