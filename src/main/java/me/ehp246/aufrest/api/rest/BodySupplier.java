package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
public interface BodySupplier {
	Object get();

	default List<? extends Annotation> annotations() {
		return List.of();
	}
}
