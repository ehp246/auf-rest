package me.ehp246.aufrest.api.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;

/**
 * @author Lei Yang
 *
 */
public class HttpUtils {
	// Methods
	public final static Set<String> METHOD_NAMES = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");
	public final static String GET = "GET";
	public final static String POST = "POST";
	public final static String PUT = "PUT";
	public final static String PATCH = "PATCH";
	public final static String DELETE = "DELETE";

	// Headers
	public final static String AUTHORIZATION = "Authorization";
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String ACCEPT = "Accept";
	public final static String BEARER = "Bearer";
	public final static String BASIC = "Basic";

	// Media types
	public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String APPLICATION_JSON = "application/json";
	public static final String TEXT_PLAIN = "text/plain";

	public static String bearer(final String token) {
		return BEARER + " " + Objects.requireNonNull(token);
	}

	public static String basic(final String username, final String password) {
		return basic(Objects.requireNonNull(username) + ":" + Objects.requireNonNull(password));
	}

	public static String basic(final String value) {
		if (value == null || value.indexOf(":") < 1) {
			throw new IllegalArgumentException("Invalid value for Basic Auth header");
		}
		return BASIC + " " + Base64.getEncoder().encodeToString((value).getBytes(StandardCharsets.UTF_8));
	}

	private HttpUtils() {
		super();
	}

}
