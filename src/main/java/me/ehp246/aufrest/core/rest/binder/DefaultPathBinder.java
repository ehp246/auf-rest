package me.ehp246.aufrest.core.rest.binder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Lei Yang
 */
public class DefaultPathBinder implements PathBinder {
    private final String baseUrl;
    private final Map<String, Integer> pathParams;

    public DefaultPathBinder(String baseUrl, Map<String, Integer> pathParams) {
        super();
        this.baseUrl = baseUrl;
        this.pathParams = Collections.unmodifiableMap(pathParams);
    }

    @Override
    public Bound apply(Object target, Object[] args) {
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

        return new Bound(baseUrl, pathArgs);
    }
}
