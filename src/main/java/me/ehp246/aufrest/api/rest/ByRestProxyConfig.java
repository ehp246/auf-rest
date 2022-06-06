package me.ehp246.aufrest.api.rest;

import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig.AuthConfig;


/**
 * Defines the configuration of a {@link ByRest} proxy. Mostly the Java object
 * equivalent of the annotation.
 * <p>
 * Values should be fully resolved. No property placeholder is supported.
 * 
 * @author Lei Yang
 *
 */
public record ByRestProxyConfig(String uri, AuthConfig auth, String timeout, String accept, String contentType,
        boolean acceptGZip, Class<?> errorType, String responseBodyHandler) {
    public ByRestProxyConfig(String uri, String timeout, String accept, String contentType) {
        this(uri, new AuthConfig(), timeout, accept, contentType, true, Object.class, "");
    }

    public static record AuthConfig(List<String> value, AuthScheme scheme) {
        public AuthConfig() {
            this(List.of(), AuthScheme.DEFAULT);
        }
    }
}
