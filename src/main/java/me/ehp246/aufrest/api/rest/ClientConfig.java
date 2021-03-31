package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

/**
 * Defines global configuration points for HttpClient.
 *
 * @author Lei Yang
 */
public interface ClientConfig {
	default Duration connectTimeout() {
		return null;
	}

	default BodyHandlerProvider bodyHandlerProvider() {
		return req -> BodyHandlers.discarding();
	}
}
