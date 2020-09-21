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
public class ContextHeader {
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

	private final static ContextHeader CONTEXT = new ContextHeader();

	private ContextHeader() {
		super();
	}

	/**
	 * Makes an un-modifiable copy of all header names and values.
	 *
	 * @return never <code>null</code>
	 */
	public static Map<String, List<String>> copy() {
		final var map = CONTEXT.headers.get();
		final var mapCopy = new HashMap<String, List<String>>();

		map.entrySet().stream().forEach(entry -> mapCopy.put(entry.getKey(), List.copyOf(entry.getValue())));

		return Collections.unmodifiableMap(mapCopy);
	}

	/**
	 * Returns the value list of the name. It can be modified.
	 *
	 * @param name
	 * @return
	 */
	public static List<String> get(final String name) {
		return CONTEXT.headers.get().computeIfAbsent(name, key -> new ArrayList<>());
	}

	/**
	 * Adds the value to the value list of the named header. Returns the live list
	 * that can be modified.
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public static void add(final String name, final String value) {
		CONTEXT.headers.get().computeIfAbsent(name, key -> new ArrayList<String>()).add(value);
	}

	/**
	 * Sets the header to the specified value. All existing values are removed.
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public static void set(final String name, final String value) {
		final var values = get(name);
		values.clear();
		values.add(value);
	}

	public static void remove(final String name) {
		CONTEXT.headers.get().remove(name);
	}

	public static void removeAll() {
		CONTEXT.headers.get().clear();
	}
}
