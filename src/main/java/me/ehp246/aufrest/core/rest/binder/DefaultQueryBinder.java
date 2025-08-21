package me.ehp246.aufrest.core.rest.binder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.core.util.OneUtil;

public class DefaultQueryBinder implements QueryBinder {
    private final Map<Integer, String> queryParams;
    private final Map<String, List<String>> queryStatic;

    public DefaultQueryBinder(Map<Integer, String> queryParams, Map<String, List<String>> queryStatic) {
        super();
        this.queryParams = queryParams;
        this.queryStatic = queryStatic;
    }

    @Override
    public Map<String, List<String>> aapply(Object target, Object[] args) {
        final var queryBound = new HashMap<String, List<String>>();
        this.queryParams.entrySet().forEach(entry -> {
            final var arg = args[entry.getKey()];
            if (arg instanceof final Map<?, ?> map) {
                map.entrySet().stream().forEach(e -> {
                    final List<String> value = e.getValue() instanceof final List<?> list
                            ? list.stream().map(Object::toString).toList()
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
        return queryBound;
    }

}
