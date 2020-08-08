package me.ehp246.aufrest.api.rest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lei Yang
 *
 */
public class HttpUtils {
	public final static Set<String> METHOD_NAMES = Arrays.stream(RequestMethod.values()).map(Enum::name)
			.collect(Collectors.toSet());

	public final static String AUTHORIZATION = "Authorization";
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String ACCEPT = "Accept";
	public final static String BEARER = "Bearer";
	public final static String BASIC = "Basic";

	private HttpUtils() {
		super();
	}

	public static String bearer(final String token) {
		return BEARER + " " + Objects.requireNonNull(token);
	}

	public static String basicAuth(final String username, final String password) {
		return basicAuth(Objects.requireNonNull(username) + ":" + Objects.requireNonNull(password));
	}

	public static String basicAuth(final String value) {
		if (value == null || value.indexOf(":") < 1) {
			throw new IllegalArgumentException("Invalid value for Basic Auth header");
		}
		return BASIC + " " + Base64.getEncoder().encodeToString((value).getBytes(StandardCharsets.UTF_8));
	}
}
