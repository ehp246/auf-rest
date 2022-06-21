package me.ehp246.aufrest.core.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Optional;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedObject {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final Object target;
    private final Class<?> type;

    public ReflectedObject(Object target) {
        this.target = target;
        this.type = target.getClass();
    }

    public Optional<MethodHandle> findPublicMethod(final String name, final Class<?> returnType,
            final List<Class<?>> paramTypes) {
        final var methodType = MethodType.methodType(returnType, paramTypes == null ? List.of() : paramTypes);
        try {
            return Optional.of(LOOKUP.findVirtual(type, name, methodType));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
