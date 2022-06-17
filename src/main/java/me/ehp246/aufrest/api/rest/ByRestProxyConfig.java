package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig.AuthConfig;


/**
 * Defines the configuration of a {@link ByRest} proxy. Mostly the Java object
 * equivalent of the annotation. *
 * <p>
 * Values should be fully resolved. No property placeholder is supported.
 * 
 * @author Lei Yang
 *
 */
public record ByRestProxyConfig(String uri, AuthConfig auth, Duration timeout, String accept, String contentType,
        boolean acceptGZip, Class<?> errorType, BodyHandler<?> responseBodyHandler) {
    public ByRestProxyConfig(String uri, Duration timeout, String accept, String contentType) {
        this(uri, new AuthConfig(), timeout, accept, contentType, true, Object.class, BodyHandlers.discarding());
    }

    public static record AuthConfig(List<String> value, AuthScheme scheme) {
        public AuthConfig() {
            this(List.of(), AuthScheme.DEFAULT);
        }
    }
}
