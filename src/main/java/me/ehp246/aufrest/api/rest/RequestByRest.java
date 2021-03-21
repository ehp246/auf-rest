package me.ehp246.aufrest.api.rest;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * The abstraction of a REST request that expects a response.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface RequestByRest {
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

	/**
	 * Specifies the target proxy object that the request is generated from. Most
	 * often the target is a {@link ByRest}-annotated interface. Could be
	 * <code>null</code>.
	 * 
	 * @return
	 */
	default InvokedOn invokedOn() {
		return null;
	}

	default Object body() {
		return null;
	}

	default Map<String, List<String>> headers() {
		return null;
	}
}
