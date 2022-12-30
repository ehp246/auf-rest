package me.ehp246.aufrest.api.rest;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The abstraction of a REST request that expects a response.
 *
 * @author Lei Yang
 *
 */
public interface RestRequest {
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
     * A non-<code>null</code> supplier indicates to the framework, it should use
     * the returned supplier for Authorization header ignoring the optional global
     * {@link AuthProvider AuthorizationProvider} bean.
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

    default Object body() {
        return null;
    }

    default ToJsonDescriptor toJsonDescriptor() {
        return this.body() == null ? null : this.body()::getClass;
    }

    /**
     * Defines application-custom headers.
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
