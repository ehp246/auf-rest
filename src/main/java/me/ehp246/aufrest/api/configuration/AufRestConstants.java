package me.ehp246.aufrest.api.configuration;

/**
 * Defines the constants supported by the framework.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 2.0
 */
public class AufRestConstants {
	/**
	 * The property name to configure the connection timeout duration for HttpClient
	 * globally.
	 * <p>
	 * The value should follow ISO 8601 Duration standard. See
	 * {@link https://en.wikipedia.org/wiki/ISO_8601#Durations}.
	 * <p>
	 * Defaults to "PT15S".
	 *
	 * @see java.net.http.HttpClient.Builder
	 */
	public static final String CONNECT_TIMEOUT = "me.ehp246.aufrest.connectTimeout";
	/**
	 * The property name to configure the timeout duration waiting for a response
	 * for a request.
	 * <p>
	 * The value should follow ISO 8601 Duration standard. See
	 * {@link https://en.wikipedia.org/wiki/ISO_8601#Durations}.
	 * <p>
	 * Defaults to "PT30S".
	 *
	 * @see java.net.http.HttpRequest.Builder
	 */
	public static final String RESPONSE_TIMEOUT = "me.ehp246.aufrest.responseTimeout";

	/**
	 * The default connection timeout in IS8601 Duration format.
	 */
	public static final String CONNECT_TIMEOUT_DEFAULT = "PT15S";
	/**
	 * The default response timeout in IS8601 Duration format.
	 */
	public static final String RESPONSE_TIMEOUT_DEFAULT = "PT30S";

	private AufRestConstants() {
		super();
	}

}
