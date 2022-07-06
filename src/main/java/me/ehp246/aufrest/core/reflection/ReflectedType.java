package me.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedType {
    private final Class<?> type;
    private final List<Method> methods;

    public ReflectedType(Class<?> type) {
        this.type = type;
        this.methods = List.of(type.getDeclaredMethods());
    }

    public Optional<Method> findMethod(final String name, final Class<?>... parameters) {
        try {
            return Optional.of(type.getMethod(name, parameters));
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Stream<Method> streamMethodsWith(Class<? extends Annotation> annotationType) {
        return methods.stream().filter(method -> method.isAnnotationPresent(annotationType));
    }
}
