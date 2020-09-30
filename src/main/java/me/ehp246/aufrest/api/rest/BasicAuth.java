package me.ehp246.aufrest.api.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A simple Basic Auth header implementation.
 * <p>
 * The class does not allow empty/blank username and/or password.
 *
 * @author Lei Yang
 * @since 1.1
 *
 */
public class BasicAuth {
	private final String value;

	/**
	 * Expects the parameter in the format of "${username}:${password}".
	 *
	 * @param usernameAndPassword
	 */
	public BasicAuth(final String usernameAndPassword) {
		super();

		if (usernameAndPassword == null || !usernameAndPassword.contains(":")) {
			throw new IllegalArgumentException();
		}

		value = "Basic " + Base64.getEncoder().encodeToString((usernameAndPassword).getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * @param username Required. No <code>null</code>, empty, or blank.
	 * @param password Required. No <code>null</code>, empty, or blank.
	 * @throws IllegalArgumentException
	 */
	public BasicAuth(final String username, final String password) {
		super();

		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException();
		}
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException();
		}

		value = "Basic "
				+ Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

	}

	public String value() {
		return value;
	}

}
