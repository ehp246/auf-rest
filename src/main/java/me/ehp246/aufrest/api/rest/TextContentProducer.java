package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Lei Yang
 *
 */
public interface TextContentProducer {
	String produce(Supplier value);

	interface Supplier {
		Object value();

		default Class<?> type() {
			return value() == null ? null : value().getClass();
		}

		default List<? extends Annotation> annotations() {
			return List.of();
		}
	}

}
