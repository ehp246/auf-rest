package me.ehp246.aufrest.core.rest;

import java.util.List;
import java.util.function.Supplier;

import me.ehp246.aufrest.api.exception.RestFnException;

/**
 * @param received  The object that is either returned or thrown by the
 *                  invocation. Could be <code>null</code> or <code>void</code>.
 * @param hasThrown Indicates the invocation didn't complete normally. It has
 *                  thrown an error/exception.
 * 
 * @author Lei Yang
 * 
 */
record RestFnOutcome(Object received, boolean hasThrown) {
    RestFnOutcome(Object received) {
        this(received, false);
    }

    /**
     * Returns the received as the value or dispatch an exception. It is assumed if
     * the received is an exception it must be either a RuntimeException or a
     * wrapped checked exception.
     * 
     * @param canThrow
     * @return
     * @throws Throwable
     */
    Object orElseThrow(final List<Class<?>> canThrow) throws Throwable {
        final var received = this.received();
        if (!this.hasThrown()) {
            return received;
        }

        // Dispatch the exception.
        if (received instanceof RuntimeException && !(received instanceof RestFnException)) {
            throw (Throwable) received;
        }
        // Assuming here.
        final var restFnEx = (RestFnException) received;
        // Checked and declared.
        if (canThrow != null
                && canThrow.stream().filter(c -> c.isAssignableFrom(restFnEx.getCause().getClass())).count() > 0) {
            throw restFnEx.getCause();
        }
        // Re-throw
        throw restFnEx;
    }

    static RestFnOutcome invoke(final Supplier<?> supplier) {
        try {
            // Call it now. Don't wait.
            return new RestFnOutcome(supplier.get());
        } catch (Exception e) {
            return new RestFnOutcome(e, true);
        }
    }
}