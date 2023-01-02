package me.ehp246.aufrest.core.rest;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.web.util.UriComponentsBuilder;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.ValueDescriptor;
import me.ehp246.aufrest.api.spi.ValueDescriptor.JsonViewValue;
import me.ehp246.aufrest.core.reflection.ArgBinder;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Builds a {@linkplain RestRequest} from an invocation on a parsed proxy
 * method.
 *
 * @author Lei Yang
 * @see DefaultProxyMethodParser
 */
final class DefaultInvocationRequestBinder implements InvocationRequestBinder {
    private final String method;
    private final String accept;
    private final String acceptEncoding;
    private final String contentType;
    private final UriComponentsBuilder uriBuilder;
    private final ArgBinder<Object, Supplier<String>> authSupplierFn;
    private final Map<String, Integer> pathParams;
    private final Map<Integer, String> queryParams;
    private final Map<String, List<String>> queryStatic;
    private final Map<Integer, String> headerParams;
    private final Map<String, List<String>> headerStatic;
    private final Duration timeout;
    // Request body related.
    private final ArgBinder<Object, Object> bodyArgBinder;
    private final JsonViewValue bodyInfo;
    // Response body
    private final ArgBinder<Object, BodyHandler<?>> consumerBinder;
    private final Function<HttpResponse<?>, ?> returnMapper;


    DefaultInvocationRequestBinder(final String method, final String accept, final boolean acceptGZip,
            final String contentType, final Duration timeout, final UriComponentsBuilder uriBuilder,
            final Map<String, Integer> pathParams, final Map<Integer, String> queryParams,
            final Map<String, List<String>> queryStatic, final Map<Integer, String> headerParams,
            final Map<String, List<String>> headerStatic,
            final ArgBinder<Object, Supplier<String>> authSupplierFn,
            final ArgBinder<Object, Object> bodyArgBinder,
            final JsonViewValue bodyInfo, final ArgBinder<Object, BodyHandler<?>> consumerBinder,
            final Function<HttpResponse<?>, ?> returnMapper) {
        super();
        this.method = method;
        this.accept = accept;
        this.acceptEncoding = acceptGZip ? "gzip" : null;
        this.contentType = contentType;
        this.uriBuilder = uriBuilder;
        this.authSupplierFn = authSupplierFn;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.queryStatic = queryStatic;
        this.headerParams = headerParams;
        this.headerStatic = headerStatic;
        this.timeout = timeout;
        this.bodyArgBinder = bodyArgBinder;
        this.bodyInfo = bodyInfo;
        this.consumerBinder = consumerBinder;
        this.returnMapper = returnMapper;
    }

    @Override
    public Bound apply(final Object target, final Object[] args) {
        final var pathArgs = new HashMap<String, Object>();
        this.pathParams.entrySet().forEach(entry -> {
            final var arg = args[entry.getValue()];
            if (arg instanceof final Map<?, ?> map) {
                pathArgs.putAll(map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(),
                        e -> URLEncoder.encode(e.getValue().toString(), StandardCharsets.UTF_8))));

                map.entrySet().stream().forEach(e -> pathArgs.putIfAbsent(e.getKey().toString(),
                        URLEncoder.encode(e.getValue().toString(), StandardCharsets.UTF_8)));
            } else {
                pathArgs.put(entry.getKey(), URLEncoder.encode(arg.toString(), StandardCharsets.UTF_8));
            }
        });

        final var uri = this.uriBuilder.buildAndExpand(pathArgs).toUriString();

        final var queryBound = new HashMap<String, List<String>>();
        this.queryParams.entrySet().forEach(entry -> {
            final var arg = args[entry.getKey()];
            if (arg instanceof final Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> queryBound.merge(e.getKey().toString(),
                        new ArrayList<>(Arrays.asList(OneUtil.toString(e.getValue()))), (o, p) -> {
                            o.add(p.get(0));
                            return o;
                        }));
            } else if (arg instanceof final List<?> list) {
                list.stream().forEach(v -> queryBound.merge(entry.getValue(),
                        new ArrayList<>(Arrays.asList(OneUtil.toString(v))), (o, p) -> {
                            o.add(p.get(0));
                            return o;
                        }));
            } else if (arg != null) {
                queryBound.merge(entry.getValue(), new ArrayList<>(Arrays.asList(OneUtil.toString(arg))), (o, p) -> {
                    o.add(p.get(0));
                    return o;
                });
            }
        });
        this.queryStatic.entrySet().forEach(entry -> queryBound.putIfAbsent(entry.getKey(), entry.getValue()));

        final var headerStaticCopy = new HashMap<String, List<String>>(this.headerStatic);
        final var headerBound = new HashMap<String, List<String>>();
        this.headerParams.entrySet().forEach(new Consumer<Entry<Integer, String>>() {
            @Override
            public void accept(final Entry<Integer, String> entry) {
                final var arg = args[entry.getKey()];
                final var name = entry.getValue();
                headerStaticCopy.remove(name);
                newValue(name, arg);
            }

            private void newValue(final String key, final Object newValue) {
                if (newValue == null) {
                    return;
                }

                if (newValue instanceof final Iterable<?> iter) {
                    iter.forEach(v -> newValue(key, v));
                    return;
                }

                // One level only. No recursive yet.
                if (newValue instanceof final Map<?, ?> map) {
                    map.entrySet().forEach(entry -> {
                        newValue(entry.getKey().toString().toLowerCase(Locale.ROOT), entry.getValue());
                    });
                    return;
                }

                headerBound.computeIfAbsent(key, v -> new ArrayList<String>()).add(newValue.toString());
            }
        });

        headerStaticCopy.entrySet().stream()
                .forEach(entry -> headerBound.putIfAbsent(entry.getKey(), entry.getValue()));

        final var authSupplier = authSupplierFn == null ? null : authSupplierFn.apply(target, args);
        final var body = bodyArgBinder == null ? null : bodyArgBinder.apply(target, args);

        final var handler = consumerBinder.apply(target, args);

        return new Bound(new RestRequest() {

            @Override
            public String method() {
                return method;
            }

            @Override
            public String uri() {
                return uri;
            }

            @Override
            public Map<String, List<String>> queries() {
                return queryBound;
            }

            @Override
            public Map<String, List<String>> headers() {
                return headerBound;
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
            public Supplier<String> authSupplier() {
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
            public ValueDescriptor bodyDescriptor() {
                return bodyInfo;
            }
        }, () -> handler, returnMapper);
    }
}
