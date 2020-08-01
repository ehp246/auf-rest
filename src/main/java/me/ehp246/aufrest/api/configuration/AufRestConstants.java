package me.ehp246.aufrest.api.configuration;

/**
 * @author Lei Yang
 *
 */
public class AufRestConstants {
	public static final String CONNECT_TIMEOUT = "me.ehp246.aufrest.connectTimeout";
	public static final String RESPONSE_TIMEOUT = "me.ehp246.aufrest.responseTimeout";

	public static final long CONNECT_TIMEOUT_DEFAULT = 15000;
	public static final long RESPONSE_TIMEOUT_DEFAULT = 30000;

	private AufRestConstants() {
		super();
	}

}
