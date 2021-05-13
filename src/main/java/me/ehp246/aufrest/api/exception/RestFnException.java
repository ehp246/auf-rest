package me.ehp246.aufrest.api.exception;

/**
 * Wraps a checked exception into a {@link RuntimeException} to dispatch
 * according to method signature.
 * 
 * @author Lei Yang
 *
 */
public final class RestFnException extends RuntimeException {
    private static final long serialVersionUID = 7172740406607274087L;

    public RestFnException(Exception e) {
        super(e);
    }
}
