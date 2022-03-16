package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Lei Yang
 */
public record RestRequestRecord(String id, String uri, String method, Duration timeout, Supplier<String> authSupplier,
        String contentType, String accept, Map<String, List<String>> headers, Map<String, List<String>> queryParams,
        Object body, BodyHandler<?> responseBodyHandler)
        implements RestRequest {
}
