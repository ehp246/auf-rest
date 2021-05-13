package me.ehp246.aufrest.core.byrest;

import java.util.List;
import java.util.function.Supplier;

import me.ehp246.aufrest.api.exception.RestFnException;

/**
 * @author Lei Yang
 *
 */
interface RestFnOutcome {
    /**
     * The object that is either returned or thrown by the invocation. Could be null
     * or void.
     * 
     * @return
     */
    Object received();

    /**
     * Indicates the invocation didn't complete normally. It has thrown an
     * error/exception.
     * 
     * @return
     */
    default boolean hasThrown() {
        return false;
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
    default Object orElseThrow(final List<Class<?>> canThrow) throws Throwable {
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
            final var returned = supplier.get();
            return () -> returned;
        } catch (Exception e) {
            return new RestFnOutcome() {
                @Override
                public Object received() {
                    return e;
                }

                @Override
                public boolean hasThrown() {
                    return true;
                }
            };
        }
    }
}