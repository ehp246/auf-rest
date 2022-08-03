package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestRequest.BodyAs;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
final class ReflectedInvocationRequestBuilder implements InvocationRequestBuilder {
    private final String method;
    private final String accept;
    private final String acceptEncoding;
    private final String contentType;
    private final UriComponentsBuilder uriBuilder;
    private final BiFunction<Object, Object[], Supplier<?>> authSupplierFn;
    private final BiFunction<Object, Object[], BodyHandler<?>> bodyHandlerFn;
    private final Map<String, Integer> pathParams;
    private final Map<Integer, String> queryParams;
    private final Map<Integer, String> headerParams;
    private final Map<String, List<String>> headerStatic;
    private final Duration timeout;
    private final BiFunction<Object, Object[], Object> bodyFn;
    private final BodyAs bodyAs;

    ReflectedInvocationRequestBuilder(final String method, final String accept, final boolean acceptGZip,
            final String contentType, final Duration timeout, final UriComponentsBuilder uriBuilder,
            final Map<String, Integer> pathParams, final Map<Integer, String> queryParams,
            final Map<Integer, String> headerParams, final Map<String, List<String>> headerStatic,
            final BiFunction<Object, Object[], Supplier<?>> authSupplierFn,
            final BiFunction<Object, Object[], BodyHandler<?>> bodyHandlerFn,
            final BiFunction<Object, Object[], Object> bodyFn, final BodyAs bodyAs) {
        super();
        this.method = method;
        this.accept = accept;
        this.acceptEncoding = acceptGZip ? "gzip" : null;
        this.contentType = contentType;
        this.uriBuilder = uriBuilder;
        this.authSupplierFn = authSupplierFn;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.headerParams = headerParams;
        this.headerStatic = headerStatic;
        this.timeout = timeout;
        this.bodyHandlerFn = bodyHandlerFn;
        this.bodyFn = bodyFn;
        this.bodyAs = bodyAs;
    }

    @Override
    public RestRequest apply(final Object target, final Object[] args) {
        final var pathArgs = new HashMap<String, Object>();
        this.pathParams.entrySet().forEach(entry -> {
            final var arg = args[entry.getValue()];
            if (arg instanceof Map<?, ?> map) {
                pathArgs.putAll(map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> UriUtils.encode(e.getValue().toString(), StandardCharsets.UTF_8))));

                map.entrySet().stream().forEach(e -> pathArgs.putIfAbsent(e.getKey().toString(),
                        UriUtils.encode(e.getValue().toString(), StandardCharsets.UTF_8)));
            } else {
                pathArgs.put(entry.getKey(), UriUtils.encode(arg.toString(), StandardCharsets.UTF_8));
            }
        });

        final var uri = this.uriBuilder.buildAndExpand(pathArgs).toUriString();

        final var queryParams = new HashMap<String, List<String>>();
        this.queryParams.entrySet().forEach(entry -> {
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
            } else if (arg != null) {
                queryParams.merge(entry.getValue(), new ArrayList<>(Arrays.asList(OneUtil.toString(arg))), (o, p) -> {
                    o.add(p.get(0));
                    return o;
                });
            }
        });

        final var headers = new HashMap<String, List<String>>(this.headerStatic);
        this.headerParams.entrySet().forEach(new Consumer<Entry<Integer, String>>() {
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

                headers.compute(key.toString(), (k, v) -> new ArrayList<String>()).add(newValue.toString());
            }
        });

        final var authSupplier = authSupplierFn == null ? null : authSupplierFn.apply(target, args);
        final var body = bodyFn == null ? null : bodyFn.apply(target, args);
        final var bodyHandler = bodyHandlerFn.apply(target, args);
        final var id = UUID.randomUUID().toString();

        return new RestRequest() {

            @Override
            public String id() {
                return id;
            }

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
            public String acceptEncoding() {
                return acceptEncoding;
            }

            @Override
            public Supplier<?> authSupplier() {
                return authSupplier;
            }

            @Override
            public Duration timeout() {
                return timeout;
            }

            @Override
            public Object body() {
                return body;
            }

            @Override
            public BodyAs bodyAs() {
                return bodyAs;
            }

            @Override
            public BodyHandler<?> responseBodyHandler() {
                return bodyHandler;
            }
        };
    }
}
