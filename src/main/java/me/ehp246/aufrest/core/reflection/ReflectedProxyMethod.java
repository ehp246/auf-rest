package me.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Lei Yang
 *
 */
public final class ReflectedProxyMethod {
    private final Class<?> declaringType;
    private final Method method;
    private final Parameter[] parameters;

    public ReflectedProxyMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.declaringType = method.getDeclaringClass();
        this.parameters = method.getParameters();
    }

    public Optional<ReflectedParameter> firstPayloadParameter(final Set<Class<? extends Annotation>> exclusions) {
        for (var i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (exclusions.stream().filter(type -> parameter.isAnnotationPresent(type)).findAny().isEmpty()) {
                return Optional.of(new ReflectedParameter(parameter, i));
            }
        }

        return Optional.empty();
    }

    public List<ReflectedParameter> allParametersWith(final Class<? extends Annotation> annotationType) {
        final var list = new ArrayList<ReflectedParameter>();

        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            if (parameter.isAnnotationPresent(annotationType)) {
                list.add(new ReflectedParameter(parameter, i));
            }
        }

        return list;
    }

    public Method method() {
        return this.method;
    }

    public Parameter getParameter(int index) {
        return this.parameters[index];
    }

    public <A extends Annotation> Optional<A> findOnMethod(final Class<A> annotationClass) {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

    public <A extends Annotation> Optional<A> findOnMethodUp(final Class<A> annotationClass) {
        final var found = method.getAnnotation(annotationClass);
        if (found != null) {
            return Optional.of(found);
        }

        return Optional.ofNullable(declaringType.getAnnotation(annotationClass));
    }
}
