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
	 *
	 * @see java.net.http.HttpClient.Builder
	 */
	public static final String CONNECT_TIMEOUT = "me.ehp246.aufrest.connectTimeout";
	/**
	 * The property name to configure the timeout duration in milli-seconds waiting
	 * for a response for a request.
	 *
	 * @see java.net.http.HttpRequest.Builder
	 */
	public static final String RESPONSE_TIMEOUT = "me.ehp246.aufrest.responseTimeout";

	/**
	 * The default connection timeout in milli-seconds.
	 */
	public static final long CONNECT_TIMEOUT_DEFAULT = 15000;
	public static final long RESPONSE_TIMEOUT_DEFAULT = 30000;

	private AufRestConstants() {
		super();
	}

}
