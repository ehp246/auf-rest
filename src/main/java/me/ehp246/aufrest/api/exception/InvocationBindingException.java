package me.ehp246.aufrest.api.exception;

/**
 * @author Lei Yang
 *
 */
public class InvocationBindingException extends RuntimeException {
    private static final long serialVersionUID = 2076889780785052997L;

    public InvocationBindingException(final Throwable cause) {
        super(cause);
    }
}
