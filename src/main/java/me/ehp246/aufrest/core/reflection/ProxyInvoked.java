package me.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import me.ehp246.aufrest.api.rest.Invocation;

/**
 * 
 * @author Lei Yang
 *
 */
public final class ProxyInvoked implements Invocation {
    private final Class<?> declaredType;
    private final Object target;
    private final Method method;
    private final List<?> args;
    private final Annotation[][] parameterAnnotations;
    private final List<Class<?>> threws;

    public ProxyInvoked(final Class<?> declaredType, final Object target, final Method method, final Object[] args) {
        this.declaredType = declaredType;
        this.target = target;
        this.method = Objects.requireNonNull(method);
        this.args = Collections.unmodifiableList(args == null ? new ArrayList<Object>() : Arrays.asList(args));
        this.parameterAnnotations = this.method.getParameterAnnotations();
        this.threws = List.of(this.method.getExceptionTypes());
    }

    @Override
    public Class<?> declaredType() {
        return this.declaredType;
    }

    @Override
    public Object target() {
        return target;
    }

    @Override
    public Method method() {
        return method;
    }

    public List<? extends Annotation> getMethodDeclaredAnnotations() {
        return List.of(method.getDeclaredAnnotations());
    }

    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public List<?> args() {
        return args;
    }

    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    public boolean hasReturn() {
        return this.method.getReturnType() != void.class && this.method.getReturnType() != Void.class;
    }

    public boolean isAsync() {
        return getReturnType().isAssignableFrom(CompletableFuture.class);
    }

    public boolean isSync() {
        return !isAsync();
    }

    /**
     * Void is considered a declared return.
     *
     * @return
     */
    public List<Class<?>> getThrows() {
        return threws;
    }

    public boolean canThrow(Class<?> type) {
        return this.threws.contains(type);
    }

    public boolean canReturn(Class<?> type) {
        return this.method.getReturnType().isAssignableFrom(type);
    }

    public List<?> filterPayloadArgs(final Set<Class<? extends Annotation>> annotations) {
        final var valueArgs = new ArrayList<>();
        for (var i = 0; i < parameterAnnotations.length; i++) {
            if (Stream.of(parameterAnnotations[i])
                    .filter(annotation -> annotations.contains(annotation.annotationType())).findAny().isPresent()) {
                continue;
            }
            valueArgs.add(args.get(i));
        }

        return valueArgs;
    }

    /**
     * Returns the value of the annotation or default if annotation is not found.
     */
    public <A extends Annotation, V> Optional<A> findOnDeclaringClass(final Class<A> annotationClass) {
        return Optional.ofNullable(this.method.getDeclaringClass().getAnnotation(annotationClass));
    }

    /**
     * Find all arguments of the given parameter type.
     *
     * @param <R>  Parameter type
     * @param type Class of the parameter type
     * @return all arguments of the given type. Could have <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public <R> List<R> findArgumentsOfType(final Class<R> type) {
        final var list = new ArrayList<R>();
        final var parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (type.isAssignableFrom(parameterTypes[i])) {
                list.add((R) args.get(i));
            }
        }
        return list;
    }

    /**
     * Looks for arguments that are annotated by the given Annotation type. Returns
     * a map with the key provided by the key supplier function, the value the
     * argument.
     *
     * @param <K>            Key from the key supplier
     * @param <V>            Argument object reference
     * @param <A>            Annotation type
     * @param annotationType
     * @param keySupplier
     * @return returned Map can be modified. Never <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public <K, V, A extends Annotation> Map<K, V> mapAnnotatedArguments(final Class<A> annotationType,
            final Function<A, K> keySupplier) {
        final var map = new HashMap<K, V>();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            final var found = Stream.of(parameterAnnotations[i])
                    .filter(annotation -> annotation.annotationType() == annotationType).findFirst();
            if (found.isEmpty()) {
                continue;
            }

            map.put(keySupplier.apply((A) found.get()), (V) args.get(i));
        }
        return map;
    }

    public <A extends Annotation> Stream<AnnotatedArgument<A>> streamOfAnnotatedArguments(
            final Class<A> annotationType) {
        final var builder = Stream.<AnnotatedArgument<A>>builder();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            final var arg = args.get(i);
            final var parameter = method.getParameters()[i];
            Stream.of(parameterAnnotations[i]).filter(annotation -> annotation.annotationType() == annotationType)
                    .map(anno -> new AnnotatedArgument<A>() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public A getAnnotation() {
                            return (A) anno;
                        }

                        @Override
                        public Object getArgument() {
                            return arg;
                        }

                        @Override
                        public Parameter getParameter() {
                            return parameter;
                        }

                    }).forEach(builder::add);
            ;
        }

        return builder.build();
    }

    public <A extends Annotation, V> Optional<V> optionalValueOnMethod(final Class<A> annotationClass,
            final Function<A, V> mapper) {
        return Optional.ofNullable(this.findOnMethod(annotationClass).map(mapper).orElse(null));
    }

    /**
     * Returns the value of the annotation on method or the provided default if the
     * annotation does not exist on the method.
     */
    public <A extends Annotation, V> V getMethodValueOf(final Class<A> annotationClass, final Function<A, V> mapper,
            final Supplier<V> supplier) {
        return this.findOnMethod(annotationClass).map(mapper).orElseGet(supplier);
    }

    public <A extends Annotation> Optional<A> findOnMethod(final Class<A> annotationClass) {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

    public <A extends Annotation> Optional<A> findOnMethodUp(final Class<A> annotationClass) {
        final var found = Optional.ofNullable(method.getAnnotation(annotationClass));
        if (found.isPresent()) {
            return found;
        }

        return Optional.ofNullable(getDeclaringClass().getAnnotation(annotationClass));
    }

    public String getMethodName() {
        return method.getName();
    }
}
