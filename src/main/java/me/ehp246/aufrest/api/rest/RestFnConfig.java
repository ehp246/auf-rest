package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;

import me.ehp246.aufrest.core.rest.AufRestConfiguration;

/**
 * Defines global configuration points for {@linkplain HttpClient}.
 *
 * @author Lei Yang
 * @since 4.1.0
 * @see HttpClient#connectTimeout()
 * @see AufRestConfiguration#clientConfig(String)
 */
public record RestFnConfig(String name) {
    public RestFnConfig() {
        this(null);
    }
}
