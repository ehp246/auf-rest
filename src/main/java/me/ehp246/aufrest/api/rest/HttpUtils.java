package me.ehp246.aufrest.api.rest;

import java.util.Set;

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
    public final static String ACCEPT = "accept";
    public static final String REQUEST_ID = "x-aufrest-request-id";
    public static final String TRACEPARENT = "traceparent";

    public final static String BEARER = "Bearer";
    public final static String BASIC = "Basic";

    public final static Set<String> RESERVED_HEADERS = Set.of(AUTHORIZATION, CONTENT_TYPE, ACCEPT, REQUEST_ID);

    // Media types
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_PLAIN = "text/plain";

    private HttpUtils() {
        super();
    }

}
