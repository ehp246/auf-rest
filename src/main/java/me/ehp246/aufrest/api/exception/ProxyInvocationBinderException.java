package me.ehp246.aufrest.api.exception;

import me.ehp246.aufrest.core.rest.ProxyInvocationBinder;

/**
 * Wraps checked exception that happens during
 * {@linkplain ProxyInvocationBinder#apply(Object, Object[])} operation.
 *
 * @author Lei Yang
 * @since 4.1.0
 */
public class ProxyInvocationBinderException extends RuntimeException {
    private static final long serialVersionUID = 2076889780785052997L;

    public ProxyInvocationBinderException(final Throwable cause) {
        super(cause);
    }
}
