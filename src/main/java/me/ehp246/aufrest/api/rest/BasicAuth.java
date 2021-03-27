package me.ehp246.aufrest.api.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A simple Basic Auth header implementation.
 * <p>
 * The class does not allow <code>null</code> username and/or password.
 *
 * @author Lei Yang
 * @since 2.0
 */
public class BasicAuth {
	private final String value;

	/**
	 * @param username Required.
	 * @param password Required.
	 * @throws IllegalArgumentException
	 */
	public BasicAuth(final String username, final String password) {
		super();

		if (username == null) {
			throw new NullPointerException("username");
		}
		if (password == null) {
			throw new NullPointerException("password");
		}

		value = "Basic "
				+ Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

	}

	public String value() {
		return value;
	}

}
