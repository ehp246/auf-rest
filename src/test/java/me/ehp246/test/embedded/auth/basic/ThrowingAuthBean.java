package me.ehp246.test.embedded.auth.basic;

/**
 * @author Lei Yang
 *
 */
public class ThrowingAuthBean {
    public Object throwRuntime(final RuntimeException exception) {
        throw exception;
    }

    public Object throwChecked(final Exception exception) throws Exception {
        throw exception;
    }
}
