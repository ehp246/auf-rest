package org.ehp246.aufrest.core.util;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
public class AnnotationUtil {
	private AnnotationUtil() {
	}

	public static boolean hasType(final List<? extends Annotation> annotations,
			final Class<? extends Annotation> annotationType) {
		return annotations == null || annotationType == null ? false
				: annotations.stream()
						.filter(annotation -> annotationType.isAssignableFrom(annotation.annotationType())).findAny()
						.isPresent();
	}
}
