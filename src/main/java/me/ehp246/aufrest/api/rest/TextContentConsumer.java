package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
public interface TextContentConsumer {
	Object consume(String text, Receiver receiver);

	interface Receiver {
		Class<?> type();

		default List<? extends Annotation> annotations() {
			return List.of();
		}
	}
}
