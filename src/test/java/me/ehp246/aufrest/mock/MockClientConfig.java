package me.ehp246.aufrest.mock;

import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderProvider;

/**
 * @author Lei Yang
 *
 */
public class MockClientConfig implements ClientConfig {
	private final HeaderProvider headerProvider;

	public MockClientConfig(final HeaderProvider headerProvider) {
		super();
		this.headerProvider = headerProvider;
	}

	@Override
	public HeaderProvider headerProvider() {
		return headerProvider;
	}

}
