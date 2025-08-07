package me.ehp246.aufrest.api.rest;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import me.ehp246.aufrest.core.util.OneUtil;

/**
 * Unless otherwise documented, the names are required and can't be
 * <code>null</code>. <code>null</code>, empty, and blank values are filtered
 * out and dropped.
 *
 * @author Lei Yang
 * @since 2.0
 */
public final class HeaderContext {
    private static final HeaderContext CONTEXT = new HeaderContext();

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
        if (!OneUtil.hasValue(name)) {
            throw new IllegalArgumentException("Invalid header name: " + name);
        }
        return CONTEXT.threadHeaders.get().computeIfAbsent(name.toLowerCase(Locale.US), key -> new ArrayList<>());
    }

    /**
     * Makes an un-modifiable deep copy of all header names and values.
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
        listOf(name).addAll(OneUtil.listValues(Objects.requireNonNull(values)));
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
        headers.entrySet().stream().filter(entry -> OneUtil.hasValue(entry.getKey()))
                .forEach(entry -> listOf(entry.getKey()).addAll(OneUtil.listValues(entry.getValue())));
    }

    /**
     * Set the header to the specified value. All existing values are removed.
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
        list.addAll(OneUtil.listValues(values));
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
     * Set the header to the value if it is non-blank and non-null. Otherwise, do
     * nothing.
     * 
     * @param name  header name
     * @param value header value. <code>null</code> and non-blank values are
     *              ignored.
     */
    public static void setIfPresent(final String name, final String value) {
        if (!OneUtil.hasValue(value)) {
            return;
        }

        HeaderContext.set(name, value);
    }

    /**
     * Remove all values of the name.
     *
     * @param name Required non-<code>null</code>
     */
    public static void remove(final String name) {
        if (!OneUtil.hasValue(name)) {
            return;
        }
        CONTEXT.threadHeaders.get().remove(name.toLowerCase(Locale.US));
    }

    /**
     * Clear and remove all names and values from the thread context.
     */
    public static void clear() {
        CONTEXT.threadHeaders.remove();
    }
}
