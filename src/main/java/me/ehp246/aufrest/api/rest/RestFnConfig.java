package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;

/**
 * Defines global configuration points for {@linkplain HttpClient}.
 *
 * @author Lei Yang
 * @since 4.1.0
 * @see HttpClient#connectTimeout() \
 */
public record RestFnConfig(String name) {
    public RestFnConfig() {
        this(null);
    }
}
