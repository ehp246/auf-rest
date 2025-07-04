package me.ehp246.aufrest.core.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import me.ehp246.aufrest.api.rest.ResponseHandler;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.reflection.ArgBinder;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Builds a {@linkplain RestRequest} from an invocation on a parsed proxy
 * method.
 *
 * @author Lei Yang
 * @see DefaultProxyMethodParser
 * @since 4.0
 */
final class DefaultProxyInvocationBinder implements ProxyInvocationBinder {
    private final String method;
    private final String accept;
    private final String acceptEncoding;
    private final String contentType;
    private final String baseUri;
    private final ArgBinder<Object, Supplier<String>> authSupplierFn;
    private final Map<String, Integer> pathParams;
    private final Map<Integer, String> queryParams;
    private final Map<String, List<String>> queryStatic;
    private final Map<Integer, String> headerParams;
    private final Map<String, List<String>> headerStatic;
    private final Duration timeout;
    // Request body related.
    private final ArgBinder<Object, Object> bodyArgBinder;
    private final JacksonTypeDescriptor typeDescriptor;
    // Response body
    private final ArgBinder<Object, BodyHandler<?>> handlerBinder;
    private final ProxyReturnMapper returnMapper;

    DefaultProxyInvocationBinder(final String method, final String accept, final boolean acceptGZip,
            final String contentType, final Duration timeout, final String baseUrl,
            final Map<String, Integer> pathParams, final Map<Integer, String> queryParams,
            final Map<String, List<String>> queryStatic, final Map<Integer, String> headerParams,
            final Map<String, List<String>> headerStatic, final ArgBinder<Object, Supplier<String>> authSupplierFn,
            final ArgBinder<Object, Object> bodyArgBinder, final JacksonTypeDescriptor bodyType,
            final ArgBinder<Object, BodyHandler<?>> consumerBinder, final ProxyReturnMapper returnMapper) {
        super();
        this.method = method;
        this.accept = accept;
        this.acceptEncoding = acceptGZip ? "gzip" : null;
        this.contentType = contentType;
        this.baseUri = baseUrl;
        this.authSupplierFn = authSupplierFn;
        this.pathParams = Collections.unmodifiableMap(pathParams);
        this.queryParams = Collections.unmodifiableMap(queryParams);
        this.queryStatic = Collections.unmodifiableMap(queryStatic);
        this.headerParams = Collections.unmodifiableMap(headerParams);
        this.headerStatic = Collections.unmodifiableMap(headerStatic);
        this.timeout = timeout;
        this.bodyArgBinder = bodyArgBinder;
        this.typeDescriptor = bodyType;
        this.handlerBinder = consumerBinder;
        this.returnMapper = returnMapper;
    }

    @Override
    public Bound apply(final Object target, final Object[] args) throws Throwable {
        final var paths = paths(args);

        final var queryBound = new HashMap<String, List<String>>();
        this.queryParams.entrySet().forEach(entry -> {
            final var arg = args[entry.getKey()];
            if (arg instanceof final Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> {
                    final List<String> value = e.getValue() instanceof final List<?> list
                            ? list.stream().map(Object::toString).collect(Collectors.toList())
                            : new ArrayList<>(Arrays.asList(OneUtil.toString(e.getValue())));
                    queryBound.merge(e.getKey().toString(), value, (o, p) -> {
                        o.add(p.get(0));
                        return o;
                    });
                });
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

        final var id = UUID.randomUUID().toString();

        return new Bound(new RestRequest() {

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
                return baseUri;
            }

            @Override
            public Map<String, ?> paths() {
                return paths;
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
            public JacksonTypeDescriptor bodyDescriptor() {
                return typeDescriptor;
            }

        }, new ResponseHandler.Provided<>(handlerBinder.apply(target, args)), returnMapper);
    }

    private Map<String, ?> paths(final Object[] args) {
        final var pathArgs = new HashMap<String, Object>();
        this.pathParams.entrySet().forEach(entry -> {
            final var arg = args[entry.getValue()];
            if (arg instanceof final Map<?, ?> map) {
                pathArgs.putAll(
                        map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Entry::getValue)));

                map.entrySet().stream().forEach(e -> pathArgs.putIfAbsent(e.getKey().toString(), e.getValue()));
            } else {
                pathArgs.put(entry.getKey(), arg);
            }
        });
        return pathArgs;
    }
}
