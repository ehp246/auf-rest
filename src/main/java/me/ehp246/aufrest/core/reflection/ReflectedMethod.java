package me.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class ReflectedMethod {
    private final Class<?> declaringType;
    private final Method method;
    private final Parameter[] parameters;
    private final Annotation[][] parameterAnnotations;
    private final List<Annotation> declaredAnnotations;
    private final List<Class<?>> exceptionTypes;


    public ReflectedMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.declaringType = method.getDeclaringClass();
        this.parameters = method.getParameters();
        this.parameterAnnotations = this.method.getParameterAnnotations();
        this.declaredAnnotations = List.of(method.getDeclaredAnnotations());
        this.exceptionTypes = List.of(method.getExceptionTypes());
    }

    public List<ReflectedParameter> filterParametersWith(final Set<Class<? extends Annotation>> excludedAnnotations, final Set<Class<?>> excludedTypes) {
        final var list = new ArrayList<ReflectedParameter>();
        for (var i = 0; i < parameterAnnotations.length; i++) {
            if (Stream.of(parameterAnnotations[i])
                    .filter(annotation -> excludedAnnotations.contains(annotation.annotationType())).findAny()
                    .isPresent()) {
                continue;
            }

            if (excludedTypes.contains(parameters[i].getType())) {
                continue;
            }

            list.add(new ReflectedParameter(parameters[i], i));
        }

        return list;
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

    public Parameter getParameter(final int index) {
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

    public List<ReflectedParameter> findArgumentsOfType(final Class<?> type) {
        final var list = new ArrayList<ReflectedParameter>();
        final var parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            if (type.isAssignableFrom(parameterTypes[i])) {
                list.add(new ReflectedParameter(parameters[i], i));
            }
        }

        return list;
    }

    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    /**
     * Returns the value of the annotation on method or the provided default if the
     * annotation does not exist on the method.
     */
    public <A extends Annotation, V> V getMethodValueOf(final Class<A> annotationClass, final Function<A, V> mapper,
            final Supplier<V> supplier) {
        return this.findOnMethod(annotationClass).map(mapper).orElseGet(supplier);
    }

    public List<? extends Annotation> getMethodDeclaredAnnotations() {
        return declaredAnnotations;
    }

    public List<Class<?>> getExceptionTypes() {
        return exceptionTypes;
    }

    /**
     * Is the given type on the <code>throws</code>. Must be explicitly declared.
     * Not on the clause doesn't mean the exception can not be thrown by the method,
     * e.g., all runtime exceptions.
     */
    public boolean isOnThrows(final Class<?> type) {
        return RuntimeException.class.isAssignableFrom(type)
                || this.exceptionTypes.stream().filter(t -> t.isAssignableFrom(type)).findAny().isPresent();
    }
}