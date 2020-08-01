package org.ehp246.aufrest.api.rest;

import java.time.Duration;

/**
 * @author Lei Yang
 *
 */
public interface HttpFnConfig {
	default AuthenticationProvider authProvider() {
		return null;
	}

	default Duration connectTimeout() {
		return null;
	}
}
