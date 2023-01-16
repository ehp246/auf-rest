package me.ehp246.aufrest.api.rest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.ehp246.aufrest.core.util.OneUtil;

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

    private static final Pattern PATTERN_VARIABLE = Pattern.compile("\\{([^/]+?)\\}");

    private HttpUtils() {
        super();
    }

    /**
     * Encodes a single URL path element. Returns <code>null</code> if the argument
     * is <code>null</code>.
     */
    public static String encodeUrlPath(final Object path) {
        if (path == null) {
            return null;
        }
        return URLEncoder.encode(path.toString(), StandardCharsets.UTF_8).replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!").replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
    }

    public static String encodeQueryString(final Map<String, List<String>> queries) {
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

    public static String encodeFormUrlBody(final Map<String, List<String>> map) {
        if (map == null || map.size() == 0) {
            return "";
        }

        final var joiner = new StringJoiner("&");
        for (final var entry : map.entrySet()) {
            final var key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            for (final var value : entry.getValue()) {
                if (OneUtil.hasValue(value)) {
                    joiner.add(String.join("=", key, URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8)));
                } else {
                    joiner.add(key);
                }
            }
        }

        return joiner.toString();
    }

    /**
     * Defines what a success status code is. I.e., within the range of 200.
     */
    public static boolean isSuccess(final int code) {
        return code >= 200 && code < 300;
    }

    /**
     * Replaces the variable if a value is provided by the mapping function.
     * Otherwise, retains the placeholder un-bound. I.e., the returned might have
     * un-matched variable names if the mapping function returns <code>null</code>.
     *
     * @param base      input with variable place holders
     * @param valueMap  name/value map
     * @param mappingFn maps a value object to String. Invoked upon each found
     *                  placeholder with the value from the value map as the
     *                  argument which could be <code>null</code>. If the function
     *                  returns <code>null</code>, the placeholder will be retained.
     */
    public static String bindPlaceholder(final String base, final Map<String, ?> valueMap,
            final Function<Object, String> mappingFn) {
        if (base == null || base.isBlank()) {
            return "";
        }

        if (base.indexOf('{') == -1 || valueMap == null || valueMap.isEmpty()) {
            return base;
        }

        final Matcher matcher = PATTERN_VARIABLE.matcher(base);
        final StringBuffer strBuf = new StringBuffer();
        while (matcher.find()) {
            final var name = matcher.group(1);
            final var value = Optional.ofNullable(mappingFn.apply(valueMap.get(name)))
                    .orElseGet(() -> "{" + name + "}");
            matcher.appendReplacement(strBuf, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(strBuf);

        return strBuf.toString();
    }
}
