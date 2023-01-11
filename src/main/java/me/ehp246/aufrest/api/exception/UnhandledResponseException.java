package me.ehp246.aufrest.api.exception;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.RestFn;

/**
 * Unchecked wrapper of the checked {@linkplain ErrorResponseException}.
 * <p>
 * Thrown by {@linkplain RestFn} when an error response is received.
 * <p>
 * A {@linkplain ByRest} method will throw this exception if it doesn't declare
 * a <code>throws</code> clause with the checked version.
 *
 * @author Lei Yang
 * @since 1.0
 *
 */
public final class UnhandledResponseException extends RuntimeException {
    private static final long serialVersionUID = 3813318541456042414L;

    public UnhandledResponseException(final ErrorResponseException cause) {
        super(cause);
    }

    /**
     * @return the status code of the response
     */
    public Integer statusCode() {
        return getCause().statusCode();
    }

    @Override
    public synchronized ErrorResponseException getCause() {
        return (ErrorResponseException) super.getCause();
    }

}
