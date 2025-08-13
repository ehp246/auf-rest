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
public final class ReflectedType<T> {
    private final Class<T> type;
    private final List<Method> methods;

    public ReflectedType(final Class<T> type) {
        this.type = type;
        this.methods = List.of(type.getDeclaredMethods());
    }

    public Optional<Method> findMethod(final String name, final Class<?>... parameters) {
        try {
            return Optional.of(type.getMethod(name, parameters));
        } catch (NoSuchMethodException | SecurityException e) {
            /*
             * Returns an blank if it does not exist.
             */
        }
        return Optional.empty();
    }

    public Stream<Method> streamMethodsWith(final Class<? extends Annotation> annotationType) {
        return methods.stream().filter(method -> method.isAnnotationPresent(annotationType));
    }

    /**
     * Returns all methods that has no parameters and returns a value.
     */
    public Stream<Method> streamSuppliersWith(final Class<? extends Annotation> annotationClass) {
        return this.streamMethodsWith(annotationClass).filter(m -> m.getParameterCount() == 0
                && (m.getReturnType() != void.class && m.getReturnType() != Void.class));
    }
}
