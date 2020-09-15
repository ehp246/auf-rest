package me.ehp246.aufrest.api.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lei Yang
 *
 */
public class HeaderContext {
	private final InheritableThreadLocal<Map<String, List<String>>> headers = new InheritableThreadLocal<Map<String, List<String>>>() {

		@Override
		protected Map<String, List<String>> childValue(final Map<String, List<String>> parentValue) {
			return new HashMap<>(parentValue);
		}

		@Override
		protected Map<String, List<String>> initialValue() {
			return new HashMap<>();
		}

	};

	private final static HeaderContext CONTEXT = new HeaderContext();

	private HeaderContext() {
		super();
	}

	public static Map<String, List<String>> headers() {
		final var map = CONTEXT.headers.get();
		final var mapCopy = new HashMap<String, List<String>>();

		map.entrySet().stream().forEach(entry -> mapCopy.put(entry.getKey(), List.copyOf(entry.getValue())));

		return Collections.unmodifiableMap(mapCopy);
	}

	public static void header(final String name, final String value) {
		CONTEXT.headers.get().computeIfAbsent(name, key -> new ArrayList<>()).add(value);
	}

	public static void remove(final String name) {
		CONTEXT.headers.get().remove(name);
	}

	public static void clear() {
		CONTEXT.headers.get().clear();
	}
}
