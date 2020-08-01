package org.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ObjectToText {
	<T> String apply(Supplier<T> object);

	interface Supplier<T> {
		T get();

		@SuppressWarnings("unchecked")
		default Class<T> type() {
			return get() == null ? null : (Class<T>) get().getClass();
		}

		default List<? extends Annotation> annotations() {
			return List.of();
		}
	}
}
