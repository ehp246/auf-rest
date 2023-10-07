package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Defines global configuration points for {@linkplain HttpClient}.
 *
 * @author Lei Yang
 * @since 4.1.0
 * @see HttpClient#connectTimeout() \
 */
public record RestFnConfig(String name, Map<String, Supplier<String>> log4jContextSuppliers) {
    public RestFnConfig() {
        this(null, null);
    }

    public RestFnConfig(final String name) {
        this(name, null);
    }
}
