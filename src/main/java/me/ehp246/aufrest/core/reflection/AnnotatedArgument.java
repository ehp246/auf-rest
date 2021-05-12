package me.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public interface AnnotatedArgument<T extends Annotation> {
    T getAnnotation();

    /**
     *
     *
     * @return argument object of the annotated parameter. Could be
     *         <code>null</code>
     */
    Object getArgument();

    Parameter getParameter();
}
