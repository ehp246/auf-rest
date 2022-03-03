package me.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author Lei Yang
 *
 */
public record AnnotatedArgument<T extends Annotation> (T annotation, Object argument, Parameter parameter) {
}
