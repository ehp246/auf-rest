package me.ehp246.aufrest.api.rest;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The abstraction of a REST request that expects a response.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 4.0
 */
public interface RestRequest {
    /**
     * Defines the URL path.
     * <p>
     * Should be a simple path without query string or other parameters.
     *
     * @see <a href=
     *      "https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL#:~:text=With%20Hypertext%20and%20HTTP%2C%20URL,unique%20resource%20on%20the%20Web.">URL</a>
     */
    String uri();

    default String method() {
        return "GET";
    }

    default Duration timeout() {
        return null;
    }

    /**
     * Defines the supplier for Authorization value for the request.
     * <p>
     * A non-<code>null</code> supplier indicates to use the returned for
     * Authorization header ignoring the optional global {@link AuthProvider
     * AuthorizationProvider} bean.
     * <p>
     * The returned object from the supplier is converted to {@linkplain String} via
     * {@linkplain Object#toString()}.
     * <p>
     * The supplier can return <code>null</code>. In which case, Authorization
     * header will not be set.
     * <p>
     * If the method returns <code>null</code>, i.e., there is no supplier,
     * Authorization will be set by the global Authorization Provider if there is
     * one.
     * <p>
     *
     *
     * @return
     */
    default Supplier<String> authSupplier() {
        return null;
    }

    default String contentType() {
        return "";
    }

    default String accept() {
        return HttpUtils.APPLICATION_JSON;
    }

    default String acceptEncoding() {
        return null;
    }

    /**
     * Defines the request body/payload. There is built-in support for the following
     * types:
     * <li>{@linkplain InputStream}</li>
     * <li>{@linkplain Path}</li>
     * <p>
     * Un-recognized Java types will be sent as <code>application/json</code>.
     */
    default Object body() {
        return null;
    }

    /**
     * Specifies application-custom headers.
     * <p>
     * The map should not include the reserved headers defined in
     * {@linkplain HttpUtils#RESERVED_HEADERS}.
     * <p>
     * {@code null} accepted.
     */
    default Map<String, List<String>> headers() {
        return null;
    }

    /**
     * The values should NOT be encoded. Encoding will be taken care of by the HTTP
     * client.
     */
    default Map<String, List<String>> queries() {
        return null;
    }
}
