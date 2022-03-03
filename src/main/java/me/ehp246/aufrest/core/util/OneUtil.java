package me.ehp246.aufrest.core.util;

import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lei Yang
 *
 */
public final class OneUtil {
    private OneUtil() {
        super();
    }

    public static String toString(final Object value) {
        return value == null ? null : value.toString();
    }

    public static String nullIfBlank(final Object value) {
        return nullIfBlank(toString(value));
    }

    public static String nullIfBlank(final String value) {
        return value != null && !value.isBlank() ? value : null;
    }

    public static boolean hasValue(final String value) {
        return value != null && !value.isBlank();
    }

    public static Stream<String> streamValues(final Collection<String> values) {
        return Optional.ofNullable(values).orElseGet(ArrayList::new).stream().filter(OneUtil::hasValue);
    }

    public static List<String> listValues(final Collection<String> values) {
        return streamValues(values).collect(Collectors.toList());
    }

    public static <V> V orElse(final Callable<V> callable, final V v) {
        try {
            return callable.call();
        } catch (final Exception e) {
            return v;
        }
    }

    public static <V> V orThrow(final Callable<V> callable) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> V orThrow(final Callable<V> callable, final String msg) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw new RuntimeException(msg, e);
        }
    }

    public static <V, X extends RuntimeException> V orThrow(final Callable<V> callable,
            final Function<Exception, X> fn) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw fn.apply(e);
        }
    }

    public static boolean isPresent(final List<? extends Annotation> annos, final Class<? extends Annotation> type) {
        return OneUtil.filter(annos, type).findAny().isPresent();
    }

    public static Stream<? extends Annotation> filter(final List<? extends Annotation> annos,
            final Class<? extends Annotation> type) {
        return Optional.ofNullable(annos).filter(Objects::nonNull).orElseGet(ArrayList::new).stream()
                .filter(anno -> anno.annotationType() == type);
    }

    public static String formUrlEncodedBody(final Map<String, List<String>> map) {
        final var joiner = new StringJoiner("&");
        for (final var entry : map.entrySet()) {
            final var key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            for (final var value : entry.getValue()) {
                if (hasValue(value)) {
                    joiner.add(String.join("=", key, URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8)));
                } else {
                    joiner.add(key);
                }
            }
        }
    
        return joiner.toString();
    }

    public static Map<String, List<String>> toQueryParamMap(final Map<String, List<Object>> input) {
        if (input == null || input.size() == 0) {
            return new HashMap<String, List<String>>();
        }

        final var map = new HashMap<String, List<String>>(input.size());
        
        for (final var entry : input.entrySet()) {
            final var values = entry.getValue();
            final var mapped = new ArrayList<String>();

            if (values != null) {
                for (final var value : values) {
                    if (value instanceof List<?> v) {
                        v.stream().map(t -> t == null ? (String) null : t.toString()).forEach(t -> mapped.add(t));
                    } else {
                        mapped.add(value == null ? null : value.toString());
                    }
                }
            }

            map.put(entry.getKey(), mapped);
        }

        return map;
    }
}
