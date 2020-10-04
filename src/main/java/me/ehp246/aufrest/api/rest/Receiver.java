package me.ehp246.aufrest.api.rest;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Receiver {
	Class<?> type();

	default List<Class<?>> reifying() {
		return List.of();
	}

	default List<? extends Annotation> annotations() {
		return List.of();
	}
}