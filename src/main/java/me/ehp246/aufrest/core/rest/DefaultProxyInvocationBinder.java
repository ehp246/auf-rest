package me.ehp246.aufrest.core.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.ArrayList;
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

import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.ResponseHandler;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.reflection.ArgBinder;
import me.ehp246.aufrest.core.rest.binder.BodyBinder;
import me.ehp246.aufrest.core.rest.binder.QueryBinder;

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
    private final String baseUri;
    private final ArgBinder<Object, Supplier<String>> authSupplierFn;
    private final Map<String, Integer> pathParams;
    private final Map<Integer, String> headerParams;
    private final Map<String, List<String>> headerStatic;
    private final Duration timeout;
    private final BodyBinder bodyBinder;
    private final QueryBinder queryBinder;
    // Response body
    private final ArgBinder<Object, BodyHandler<?>> handlerBinder;
    private final ProxyReturnMapper returnMapper;

    DefaultProxyInvocationBinder(final String method, final String accept, final boolean acceptGZip,
            final Duration timeout, final String baseUrl, final Map<String, Integer> pathParams,
            final QueryBinder queryBinder, final Map<Integer, String> headerParams,
            final Map<String, List<String>> headerStatic, final ArgBinder<Object, Supplier<String>> authSupplierFn,
            final BodyBinder bodyBinder, final ArgBinder<Object, BodyHandler<?>> consumerBinder,
            final ProxyReturnMapper returnMapper) {
        super();
        this.method = method;
        this.accept = accept;
        this.acceptEncoding = acceptGZip ? "gzip" : null;
        this.baseUri = baseUrl;
        this.authSupplierFn = authSupplierFn;
        this.pathParams = Collections.unmodifiableMap(pathParams);
        this.headerParams = Collections.unmodifiableMap(headerParams);
        this.headerStatic = Collections.unmodifiableMap(headerStatic);
        this.timeout = timeout;
        this.bodyBinder = bodyBinder;
        this.queryBinder = queryBinder;
        this.handlerBinder = consumerBinder;
        this.returnMapper = returnMapper;
    }

    @Override
    public Bound apply(final Object target, final Object[] args) throws Throwable {
        final var paths = paths(args);

        final var boundQuery = this.queryBinder.aapply(target, args);

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
                    map.entrySet().forEach(
                            entry -> newValue(entry.getKey().toString().toLowerCase(Locale.ROOT), entry.getValue()));
                    return;
                }

                headerBound.computeIfAbsent(key, v -> new ArrayList<String>()).add(newValue.toString());
            }
        });

        headerStaticCopy.entrySet().stream()
                .forEach(entry -> headerBound.putIfAbsent(entry.getKey(), entry.getValue()));

        final var authSupplier = authSupplierFn == null ? null : authSupplierFn.apply(target, args);

        final var boundBody = this.bodyBinder.apply(target, args);

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
            public Map<String, Object> paths() {
                return paths;
            }

            @Override
            public Map<String, List<String>> queries() {
                return boundQuery;
            }

            @Override
            public Map<String, List<String>> headers() {
                return headerBound;
            }

            @Override
            public String contentType() {
                return boundBody.contentType();
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
                return boundBody.body();
            }

            @Override
            public JacksonTypeDescriptor bodyDescriptor() {
                return boundBody.bodyDescriptor();
            }

        }, new ResponseHandler.Provided<>(handlerBinder.apply(target, args)), returnMapper);
    }

    private Map<String, Object> paths(final Object[] args) {
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
