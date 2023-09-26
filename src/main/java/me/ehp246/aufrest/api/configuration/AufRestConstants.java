package me.ehp246.aufrest.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Defines the constants supported by the framework.
 *
 * @author Lei Yang
 * @version 3.2
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
    public static final String CONNECT_TIMEOUT = "me.ehp246.aufrest.connect-timeout";
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
    public static final String RESPONSE_TIMEOUT = "me.ehp246.aufrest.response-timeout";

    /**
     * To configure a built-in logger.
     */
    public static final String REST_LOGGER_ENABLED = "me.ehp246.aufrest.restlogger.enabled";
    public static final String REST_LOGGER_MASKED = "me.ehp246.aufrest.restlogger.masked-headers";

    /**
     * Can be used to customize an {@linkplain ObjectMapper} for Auf REST.
     */
    public static final String AUF_REST_OBJECT_MAPPER = "aufRestObjectMapper";

    public static final String AUFRESTREQUESTID = "AufRestRequestId";

    private AufRestConstants() {
        super();
    }

}
