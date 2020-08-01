package org.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;

public interface AnnotatedArgument<T extends Annotation> {
	T getAnnotation();
	Object getArgument();
}
