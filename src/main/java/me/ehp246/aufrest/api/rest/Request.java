package me.ehp246.aufrest.api.rest;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * The abstraction of a REST request that expects a response.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 2.1
 */
public interface Request extends Message {
	String uri();

	default String method() {
		return "GET";
	}

	default String id() {
		return null;
	}

	default Duration timeout() {
		return null;
	}

	/**
	 * Defines the supplier for Authorization value for the request.
	 * <p>
	 * A non-<code>null</code> supplier indicates to the framework, it should use
	 * the returned supplier for Authorization header ignoring the global
	 * {@link AuthorizationProvider AuthorizationProvider} bean. The supplier can
	 * return <code>null</code>. In which case, the header will not be set.
	 * <p>
	 * If <code>null</code> is returned, the framework uses the global
	 * AuthorizationProvider.
	 *
	 * @return
	 */
	default Supplier<String> authSupplier() {
		return null;
	}

	default String contentType() {
		return HttpUtils.APPLICATION_JSON;
	}

	default String accept() {
		return HttpUtils.APPLICATION_JSON;
	}

	default BodyReceiver bodyReceiver() {
		return null;
	}
}
