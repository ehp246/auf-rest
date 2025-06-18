package me.ehp246.aufrest.api.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author Lei Yang
 */
public final class ParameterizedTypeBuilder {
    private ParameterizedTypeBuilder() {
    }

    public static ParameterizedType of(final Type rawType, final Type typeArg) {
        return ParameterizedTypeBuilder.of(null, rawType, new Type[] { typeArg });
    }

    public static ParameterizedType ofMap(final Type... typeArgs) {
        return ParameterizedTypeBuilder.of(null, Map.class, typeArgs);
    }

    public static ParameterizedType ofList(final Type typeArg) {
        return ParameterizedTypeBuilder.of(null, List.class, typeArg);
    }

    public static ParameterizedType of(final Type ownerType, final Type rawType, final Type... typeArgs) {
        return new ParameterizedType() {

            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return ownerType;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return typeArgs;
            }
        };
    }
}
