package me.ehp246.aufrest.core.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedType {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final Class<?> type;

    public ReflectedType(Class<?> type) {
        this.type = type;
    }

    public Optional<MethodHandle> findPublicMethod(final String name, final Class<?> returnType,
            final Class<?>... paramTypes) {
        final var methodType = MethodType.methodType(returnType, paramTypes == null ? new Class<?>[] {} : paramTypes);
        try {
            return Optional.of(LOOKUP.findVirtual(type, name, methodType));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
