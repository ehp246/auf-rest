package me.ehp246.aufrest.api.rest;

/**
 * A simple Bearer Token implementation.
 * <p>
 * The class does not allow empty/blank token.
 *
 * @author Lei Yang
 * @since 1.1
 */
public class BearerToken {
	private final String value;

	public BearerToken(final String token) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException();
		}
		value = "Bearer " + token;
	}

	public String value() {
		return value;
	}

}
