package me.ehp246.aufrest.api.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Lei Yang
 *
 */
public class HeaderContext {
	private final InheritableThreadLocal<Map<String, List<String>>> headers = new InheritableThreadLocal<Map<String, List<String>>>() {

		@Override
		protected Map<String, List<String>> childValue(final Map<String, List<String>> parentValue) {
			final var copy = new HashMap<String, List<String>>();
			parentValue.entrySet().stream().forEach(entry -> {
				copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
			});
			return copy;
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

	public static Map<String, List<String>> getAll() {
		final var map = CONTEXT.headers.get();
		final var mapCopy = new HashMap<String, List<String>>();

		map.entrySet().stream().forEach(entry -> mapCopy.put(entry.getKey(), List.copyOf(entry.getValue())));

		return Collections.unmodifiableMap(mapCopy);
	}

	public static List<String> get(final String name) {
		return Collections
				.unmodifiableList(Optional.ofNullable(CONTEXT.headers.get().get(name)).orElseGet(ArrayList::new));
	}

	public static void add(final String name, final String value) {
		CONTEXT.headers.get().computeIfAbsent(name, key -> new ArrayList<>()).add(value);
	}

	public static void remove(final String name) {
		CONTEXT.headers.get().remove(name);
	}

	public static void clear() {
		CONTEXT.headers.get().clear();
	}
}
