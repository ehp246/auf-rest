package me.ehp246.aufrest.core.rest;

import java.util.function.Supplier;

import me.ehp246.aufrest.api.rest.RestFn;

/**
 * Used by {@linkplain ByRestProxyFactory#newInstance(Class)} to wrap the
 * outcome of an invocation on {@linkplain RestFn}.
 *
 * @param received  The object that is either returned or thrown by the
 *                  invocation. Could be <code>null</code> or <code>void</code>.
 * @param hasThrown Indicates the invocation didn't complete normally. It has
 *                  thrown an error/exception.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 4.0
 */
record FnOutcome(Object received, boolean hasThrown) {
    FnOutcome(final Object received) {
        this(received, false);
    }

    static FnOutcome invoke(final Supplier<?> supplier) {
        try {
            // Call it now. Don't wait.
            return new FnOutcome(supplier.get());
        } catch (final Exception e) {
            return new FnOutcome(e, true);
        }
    }
}