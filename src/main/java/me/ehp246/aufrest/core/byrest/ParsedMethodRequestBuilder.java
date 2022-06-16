package me.ehp246.aufrest.core.byrest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class ParsedMethodRequestBuilder implements ProxyToRestFn {
    private final String method;
    private final String accept;
    private final String contentType;
    private final UriComponentsBuilder uriBuilder;
    private final BiFunction<Object, Object[], Supplier<String>> authSupplierFn;
    private final Map<String, Integer> pathMap;
    private final Map<Integer, String> queryMap;
    private final Map<Integer, String> headerMap;
    private final Map<String, List<String>> reservedHeaders;

    public ParsedMethodRequestBuilder(String method, String accept, String contentType, UriComponentsBuilder uriBuilder,
            BiFunction<Object, Object[], Supplier<String>> authSupplierFn, Map<String, Integer> pathMap,
            Map<Integer, String> queryMap, final Map<Integer, String> headerMap,
            Map<String, List<String>> reservedHeaders) {
        super();
        this.method = method;
        this.accept = accept;
        this.contentType = contentType;
        this.uriBuilder = uriBuilder;
        this.authSupplierFn = authSupplierFn;
        this.pathMap = pathMap;
        this.queryMap = queryMap;
        this.headerMap = headerMap;
        this.reservedHeaders = reservedHeaders;
    }

    @Override
    public RestRequest apply(final Object target, final Object[] args) {
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

        final var headers = new HashMap<String, List<String>>();
        this.headerMap.entrySet().stream().forEach(new Consumer<Entry<Integer, String>>() {
            @Override
            public void accept(Entry<Integer, String> entry) {
                final var arg = args[entry.getKey()];
                newValue(entry.getValue(), arg);
            }

            private void newValue(final Object key, final Object newValue) {
                if (newValue == null) {
                    return;
                }

                if (newValue instanceof Iterable<?> iter) {
                    iter.forEach(v -> newValue(key, v));
                    return;
                }

                if (newValue instanceof Map<?, ?> map) {
                    map.entrySet().forEach(entry -> {
                        newValue(entry.getKey(), entry.getValue());
                    });
                    return;
                }

                getMapped(key).add(newValue.toString());
            }

            private List<String> getMapped(final Object key) {
                return headers.computeIfAbsent(key.toString(), k -> new ArrayList<String>());
            }
        });
        headers.putAll(reservedHeaders);

        final var authSupplier = authSupplierFn == null ? null : authSupplierFn.apply(target, args);

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
