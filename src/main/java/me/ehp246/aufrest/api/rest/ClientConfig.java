package me.ehp246.aufrest.api.rest;

import java.time.Duration;

/**
 * @author Lei Yang
 *
 */
public interface ClientConfig {
	default Duration connectTimeout() {
		return null;
	}

	default Duration requestTimeout() {
		return null;
	}
}
