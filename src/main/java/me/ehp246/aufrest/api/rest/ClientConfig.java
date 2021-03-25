package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;

/**
 * Defines global configuration points for HttpClient.
 *
 * @author Lei Yang
 */
public interface ClientConfig {
	default Duration connectTimeout() {
		return null;
	}

	default Duration responseTimeout() {
		return null;
	}

	default AuthorizationProvider authProvider() {
		return null;
	}

	default HeaderProvider headerProvider() {
		return null;
	}

	default List<RestConsumer> restConsumers() {
		return List.of();
	}
	
	default BodyPublisherProvider bodyPublisherProvider() {
		return req -> BodyPublishers.noBody();
	}

	default BodyHandlerProvider bodyHandlerProvider() {
		return req -> BodyHandlers.discarding();
	}
}
