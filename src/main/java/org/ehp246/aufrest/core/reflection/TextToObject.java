package org.ehp246.aufrest.core.reflection;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface TextToObject {
	<T> T apply(String text, Receiver<T> receiver);

	interface Receiver<T> {
		Class<? extends T> type();

		default List<? extends Annotation> annotations() {
			return List.of();
		}

		default void receive(final T value) {

		}
	}

}
