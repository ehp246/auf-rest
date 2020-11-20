package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface BodyReceiver {
	/**
	 * Type of the response body. Can be a generic container, e.g., {@link List}.
	 *
	 * @return
	 */
	Class<?> type();

	default List<Class<?>> reifying() {
		return List.of();
	}

	default List<? extends Annotation> annotations() {
		return List.of();
	}
}