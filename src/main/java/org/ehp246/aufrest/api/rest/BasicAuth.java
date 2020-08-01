package org.ehp246.aufrest.api.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * @author Lei Yang
 *
 */
public interface BasicAuth extends Authentication {
	String username();

	String password();

	@Override
	default String header() {
		return "Basic " + Base64.getEncoder()
				.encodeToString((Objects.requireNonNull(username()) + ":" + Objects.requireNonNull(password()))
						.getBytes(StandardCharsets.UTF_8));
	}
}
