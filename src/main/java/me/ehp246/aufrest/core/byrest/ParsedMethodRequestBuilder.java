package me.ehp246.aufrest.core.byrest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
record ParsedMethodRequestBuilder(String method, String accept, String contentType, UriComponentsBuilder uriBuilder,
        Function<Object[], Supplier<String>> authSupplierFn, Map<String, Integer> pathMap, Map<Integer, String> queryMap,
        Map<String, List<String>> defaultHeaders) {

    public RestRequest apply(final Object[] args) {
        final var pathArgs = new HashMap<String, Object>();
        this.pathMap.entrySet().stream().forEach(entry -> {
            final var arg = args[entry.getValue()];
            if (arg instanceof Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> pathArgs.putIfAbsent(e.getKey().toString(),
                        UriUtils.encode(e.getValue().toString(), StandardCharsets.UTF_8)));
            } else {
                pathArgs.put(entry.getKey(), UriUtils.encode(arg.toString(), StandardCharsets.UTF_8));
            }
        });

        final var uri = this.uriBuilder.buildAndExpand(pathArgs).toUriString();

        final var queryParams = new HashMap<String, List<String>>();
        this.queryMap.entrySet().forEach(entry -> {
            final var arg = args[entry.getKey()];
            if (arg instanceof Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> queryParams.merge(e.getKey().toString(),
                        new ArrayList<>(Arrays.asList(OneUtil.toString(e.getValue()))), (o, p) -> {
                            o.add(p.get(0));
                            return o;
                        }));
            } else if (arg instanceof List<?> list) {
                list.stream().forEach(v -> queryParams.merge(entry.getValue(),
                        new ArrayList<>(Arrays.asList(OneUtil.toString(v))), (o, p) -> {
                            o.add(p.get(0));
                            return o;
                        }));
            } else {
                queryParams.merge(entry.getValue(), new ArrayList<>(Arrays.asList(OneUtil.toString(arg))), (o, p) -> {
                    o.add(p.get(0));
                    return o;
                });
            }
        });

        final var headers = new HashMap<String, List<String>>(defaultHeaders);
        final var authSupplier = authSupplierFn.apply(args);

        return new RestRequest() {

            @Override
            public String method() {
                return method;
            }

            @Override
            public String uri() {
                return uri;
            }

            @Override
            public Map<String, List<String>> queryParams() {
                return queryParams;
            }

            @Override
            public Map<String, List<String>> headers() {
                return headers;
            }

            @Override
            public String contentType() {
                return contentType;
            }

            @Override
            public String accept() {
                return accept;
            }

            @Override
            public Supplier<String> authSupplier() {
                return authSupplier;
            }

        };
    }
}
