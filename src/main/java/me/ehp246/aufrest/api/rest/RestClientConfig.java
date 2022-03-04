package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

/**
 * Defines global configuration points for HttpClient.
 *
 * @author Lei Yang
 */
public record RestClientConfig(Duration connectTimeout, BodyHandlerProvider bodyHandlerProvider) {
    public RestClientConfig() {
        this(null, req -> BodyHandlers.discarding());
    }

    public RestClientConfig(Duration connectTimeout) {
        this(connectTimeout, req -> BodyHandlers.discarding());
    }
}
