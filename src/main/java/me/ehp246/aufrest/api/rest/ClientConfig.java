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

	default AuthProvider authProvider() {
		return null;
	}

	default HeaderProvider headerProvider() {
		return null;
	}

	default List<RestObserver> restObservers() {
		return List.of();
	}
	
	default BodyPublisherProvider bodyPublisherProvider() {
		return req -> BodyPublishers.noBody();
	}

	default BodyHandlerProvider bodyHandlerProvider() {
		return req -> BodyHandlers.discarding();
	}
}
