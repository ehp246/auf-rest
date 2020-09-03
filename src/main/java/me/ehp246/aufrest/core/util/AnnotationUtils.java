package me.ehp246.aufrest.core.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Lei Yang
 *
 */
public class AnnotationUtils {

	private AnnotationUtils() {
		super();
	}

	public static boolean contains(final List<? extends Annotation> annos, final Class<? extends Annotation> type) {
		return filter(annos, type).findAny().isPresent();
	}

	public static Stream<? extends Annotation> filter(final List<? extends Annotation> annos,
			final Class<? extends Annotation> type) {
		return Optional.ofNullable(annos).filter(Objects::nonNull).orElseGet(ArrayList::new).stream()
				.filter(anno -> anno.annotationType() == type);
	}
}
