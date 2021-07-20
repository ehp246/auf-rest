package me.ehp246.aufrest.mock;

import me.ehp246.aufrest.api.rest.ByRestProxyConfig;

/**
 * @author Lei Yang
 *
 */
public class MockByRestProxyConfig implements ByRestProxyConfig {

    @Override
    public String uri() {
        return "";
    }

    @Override
    public String timeout() {
        return null;
    }

    @Override
    public String accept() {
        return null;
    }

    @Override
    public String contentType() {
        return null;
    }

}
