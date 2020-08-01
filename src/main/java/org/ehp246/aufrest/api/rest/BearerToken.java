package org.ehp246.aufrest.api.rest;

import java.util.Objects;

/**
 * @author Lei Yang
 *
 */
public interface BearerToken extends Authentication {
	String token();

	@Override
	default String header() {
		return "Bearer " + Objects.requireNonNull(token());
	}
}
