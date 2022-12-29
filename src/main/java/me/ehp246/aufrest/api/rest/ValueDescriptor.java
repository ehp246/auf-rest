package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Defines the details of a value object outside of the value itself. It could
 * include the declared type, annotations, and etc. Most often the object is the
 * body of a {@linkplain RestRequest}. Used to support serialization features.
 * E.g., {@linkplain JsonView} support, and declared type recognition for
 * {@linkplain ObjectMapper#writerFor(Class)}.
 *
 * @author Lei Yang
 * @since 3.1.2
 *
 */
public final class ValueDescriptor {
    private final Class<?> type;
    private final Map<Class<? extends Annotation>, Annotation> annotationMap;
    private final Class<?> firstJsonViewValue;

    public ValueDescriptor(final Class<?> type, final Annotation[] annotations) {
        this.type = type;
        this.annotationMap = annotations == null ? Map.of()
                : Arrays.asList(annotations).stream()
                .collect(Collectors.toUnmodifiableMap(Annotation::annotationType, Function.identity()));
        this.firstJsonViewValue = Optional.ofNullable(annotationMap.get(JsonView.class)).map(ann -> (JsonView) ann)
                .map(JsonView::value).filter(value -> value.length > 0).map(value -> value[0]).orElse(null);
    }

    public Class<?> type() {
        return this.type;
    }

    public Map<Class<? extends Annotation>, Annotation> annotationMap() {
        return this.annotationMap;
    }

    public Class<?> firstJsonViewValue() {
        return this.firstJsonViewValue;
    }
}