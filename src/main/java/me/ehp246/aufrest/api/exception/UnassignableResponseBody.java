package me.ehp246.aufrest.api.exception;

/**
 * @author Lei Yang
 *
 */
public class UnassignableResponseBody extends RuntimeException {
    private static final long serialVersionUID = -4958240279774613121L;

    private final Class<?> left;
    private final Class<?> right;

    public UnassignableResponseBody(Class<?> left, Class<?> right) {
        super();
        this.left = left;
        this.right = right;
    }

}
