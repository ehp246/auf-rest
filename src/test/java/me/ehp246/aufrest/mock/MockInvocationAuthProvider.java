package me.ehp246.aufrest.mock;

import me.ehp246.aufrest.api.rest.InvocationAuthProvider;
import me.ehp246.aufrest.api.spi.Invocation;

/**
 * @author Lei Yang
 *
 */
public final class MockInvocationAuthProvider implements InvocationAuthProvider {
    private Invocation invocation;
    private final String header;

    public MockInvocationAuthProvider(String header) {
        super();
        this.header = header;
    }

    @Override
    public String get(Invocation invocation) {
        this.invocation = invocation;
        return header;
    }

    public Invocation takeInvocation() {
        final var invocation = this.invocation;
        this.invocation = null;
        return invocation;
    }

    public String header() {
        return this.header;
    }
}
