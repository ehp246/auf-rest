package me.ehp246.aufrest.api.configuration;

/**
 * Defines the constants supported by the framework.
 *
 * @author Lei Yang
 */
public class AufRestConstants {
	/**
	 * The property name to configure the connection timeout duration in
	 * milli-seconds for HttpClient.
	 * <p>
	 * Defaults to 15000 milliseconds.
	 *
	 * @see java.net.http.HttpClient.Builder
	 */
	public static final String CONNECT_TIMEOUT = "me.ehp246.aufrest.connectTimeout";
	/**
	 * The property name to configure the timeout duration in milliseconds waiting
	 * for a response for a request.
	 * <p>
	 * Defaults to 30000 milli-seconds.
	 *
	 * @see java.net.http.HttpRequest.Builder
	 */
	public static final String RESPONSE_TIMEOUT = "me.ehp246.aufrest.responseTimeout";

	/**
	 * The default connection timeout in milliseconds.
	 */
	public static final String CONNECT_TIMEOUT_DEFAULT = "PT15S";
	/**
	 * The default response timeout in milliseconds.
	 */
	public static final String RESPONSE_TIMEOUT_DEFAULT = "PT30S";

	private AufRestConstants() {
		super();
	}

}
