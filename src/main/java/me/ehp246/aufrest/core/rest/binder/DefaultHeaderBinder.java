package me.ehp246.aufrest.core.rest.binder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.ehp246.aufrest.core.reflection.ArgBinder;

/**
 * @author Lei Yang
 */
public class DefaultHeaderBinder implements HeaderBinder {
    private final String accept;
    private final String acceptEncoding;
    private final ArgBinder<Object, Supplier<String>> authSupplierFn;
    private final Map<Integer, String> headerParams;
    private final Map<String, List<String>> headerStatic;

    public DefaultHeaderBinder(String accept, boolean acceptGZip, ArgBinder<Object, Supplier<String>> authSupplierFn,
            Map<Integer, String> headerParams, Map<String, List<String>> headerStatic) {
        super();
        this.accept = accept;
        this.acceptEncoding = acceptGZip ? "gzip" : null;
        this.authSupplierFn = authSupplierFn;
        this.headerParams = Collections.unmodifiableMap(headerParams);
        this.headerStatic = Collections.unmodifiableMap(headerStatic);
    }

    @Override
    public Bound apply(Object target, Object[] args) throws Throwable {
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

        return new Bound(headerBound, accept, acceptEncoding, authSupplier);
    }

}
