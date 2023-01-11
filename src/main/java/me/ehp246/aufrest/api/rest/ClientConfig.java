package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Defines global configuration points for HttpClient.
 *
 * @author Lei Yang
 * @since 1.0
 * @see {@linkplain HttpClient#connectTimeout()}
 */
public record ClientConfig(Duration connectTimeout) {
    public ClientConfig() {
        this(null);
    }
}
