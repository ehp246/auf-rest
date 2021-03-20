package me.ehp246.aufrest.api.rest;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Defines global configuration points for HttpClient.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 2.1
 */
public interface ClientConfig {
	default Duration connectTimeout() {
		return null;
	}

	default Duration responseTimeout() {
		return null;
	}

	default Set<BodyFn> bodyFns() {
		return Set.of();
	}

	default AuthorizationProvider authProvider() {
		return null;
	}

	default HeaderProvider headerProvider() {
		return null;
	}

	default List<RequestFilter> requestFilters() {
		return List.of();
	}
}
