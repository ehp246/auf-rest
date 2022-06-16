package me.ehp246.aufrest.mock;

import me.ehp246.aufrest.api.rest.InvocationAuthProvider;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;

/**
 * @author Lei Yang
 *
 */
public final class MockInvocationAuthProviderResolver implements InvocationAuthProviderResolver {
    private int count = 0;
    private String name;
    private final MockInvocationAuthProvider provider;

    public MockInvocationAuthProviderResolver(MockInvocationAuthProvider provider) {
        super();
        this.provider = provider;
    }

    @Override
    public InvocationAuthProvider get(String name) {
        this.count++;
        this.name = name;
        return provider;
    }

    public String takeName() {
        final var name = this.name;
        this.name = null;
        return name;
    }

    public MockInvocationAuthProvider provider() {
        return this.provider;
    }

    public int count() {
        return this.count;
    }
}
