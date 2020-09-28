package me.ehp246.aufrest.api.rest;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import me.ehp246.aufrest.core.util.FunctionUtils;

/**
 * Unless otherwise documented, the names are required and can't be
 * <code>null</code>. <code>null</code>, empty, and blank values are filtered
 * out and dropped.
 *
 * @author Lei Yang
 *
 */
public class HeaderContext {
	private final static HeaderContext CONTEXT = new HeaderContext();

	private final ThreadLocal<Map<String, List<String>>> threadHeaders = ThreadLocal.withInitial(HashMap::new);

	private HeaderContext() {
		super();
	}

	/**
	 * For internal access.
	 *
	 * @param name
	 * @return
	 */
	private static List<String> listOf(final String name) {
		return CONTEXT.threadHeaders.get().computeIfAbsent(name.toLowerCase(Locale.US), key -> new ArrayList<>());
	}

	/**
	 * Makes an un-modifiable copy of all header names and values.
	 * <p>
	 * Keys of returned map is case-insensitive.
	 *
	 * @return never <code>null</code>
	 */
	public static Map<String, List<String>> map() {
		return HttpHeaders.of(CONTEXT.threadHeaders.get(), (name, value) -> true).map();
	}

	/**
	 * Returns the value list of the name.
	 * <p>
	 * Returned valued is un-modifiable.
	 *
	 * @param name Can not be <code>null</code>
	 * @return empty list if name does not exist. Never <code>null</code>.
	 */
	public static List<String> values(final String name) {
		return Collections.unmodifiableList(listOf(name));
	}

	/**
	 * Adds the value to the value list of the named header.
	 *
	 * @param name  required. Non-<code>null</code>
	 * @param value
	 */
	public static void add(final String name, final String value) {
		listOf(name).add(Objects.requireNonNull(value));
	}

	/**
	 * Add the list of values in the given order.
	 *
	 * @param name
	 * @param values <code>null</code> is no-op.
	 */
	public static void add(final String name, final List<String> values) {
		listOf(name).addAll(FunctionUtils.listValues(Objects.requireNonNull(values)));
	}

	/**
	 * Merge all names and values.
	 * <p>
	 * All null, empty, and blank names and values are filtered and dropped.
	 * <p>
	 * Non-existing names will be added.
	 * <p>
	 * If the name exists, the values are added.
	 *
	 * @param headers Can not be <code>null</code>.
	 */
	public static void merge(final Map<String, List<String>> headers) {
		headers.entrySet().stream().filter(entry -> FunctionUtils.hasValue(entry.getKey()))
				.forEach(entry -> listOf(entry.getKey()).addAll(FunctionUtils.listValues(entry.getValue())));
	}

	/**
	 * Sets the header to the specified value. All existing values are removed.
	 *
	 * @param name  Can not be <code>null</code>.
	 * @param value Can not be <code>null</code>.
	 */
	public static void set(final String name, final String value) {
		final var values = listOf(name);
		values.clear();
		values.add(Objects.requireNonNull(value));
	}

	public static void set(final String name, final List<String> values) {
		final var list = listOf(name);
		list.clear();
		list.addAll(FunctionUtils.listValues(values));
	}

	/**
	 * Clear all existing names and values, then set to the given map.
	 *
	 * @param headers
	 */
	public static void set(final Map<String, List<String>> headers) {
		clear();
		merge(headers);
	}

	/**
	 * Remove all values of the name.
	 *
	 * @param name Required non-<code>null</code>
	 */
	public static void remove(final String name) {
		CONTEXT.threadHeaders.get().remove(name.toLowerCase(Locale.US));
	}

	/**
	 * Clear all names and values.
	 */
	public static void clear() {
		CONTEXT.threadHeaders.get().clear();
	}
}
