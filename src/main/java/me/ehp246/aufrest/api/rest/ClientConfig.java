package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.time.Duration;

import me.ehp246.aufrest.core.rest.AufRestConfiguration;

/**
 * Defines global configuration points for {@linkplain HttpClient}.
 *
 * @author Lei Yang
 * @since 1.0
 * @see {@linkplain HttpClient#connectTimeout()},
 *      {@linkplain AufRestConfiguration#clientConfig(String)}
 */
public record ClientConfig(Duration connectTimeout) {
    public ClientConfig() {
        this(null);
    }
}
