package me.ehp246.aufrest.api.rest;

import java.time.Duration;

/**
 * Defines global configuration points for HttpClient.
 *
 * @author Lei Yang
 */
public record ClientConfig(Duration connectTimeout) {
    public ClientConfig() {
        this(null);
    }
}
