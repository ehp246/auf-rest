package me.ehp246.aufrest.api.configuration;

/**
 * Defines the constants supported by the framework.
 *
 * @author Lei Yang
 * 
 */
public final class AufRestConstants {
	/**
	 * The property name to configure the connection timeout duration for HttpClient
	 * globally.
	 * <p>
	 * The value should follow ISO 8601 Duration standard.
	 * <p>
	 * Not set by default. I.e., it never times out.
	 *
	 * @see java.net.http.HttpClient.Builder
	 * @see <a href= "https://en.wikipedia.org/wiki/ISO_8601#Durations">ISO_8601
	 *      Durations</a>
	 */
	public static final String CONNECT_TIMEOUT = "me.ehp246.aufrest.connectTimeout";
	/**
	 * The property name to configure the timeout duration waiting for a response
	 * for a request.
	 * <p>
	 * The value should follow ISO 8601 Duration standard.
	 * <p>
	 * Not set by default. I.e., it never times out.
	 *
	 * @see java.net.http.HttpRequest.Builder
	 * @see <a href= "https://en.wikipedia.org/wiki/ISO_8601#Durations">ISO_8601
	 *      Durations</a>
	 */
	public static final String RESPONSE_TIMEOUT = "me.ehp246.aufrest.responseTimeout";

	private AufRestConstants() {
		super();
	}

}
