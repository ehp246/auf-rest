package me.ehp246.aufrest.api.rest;

import java.time.Duration;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * Defines the configuration of a {@link ByRest} proxy. Mostly the Java object
 * equivalent of the annotation.
 * 
 * @author Lei Yang
 *
 */
public interface ByRestProxyConfig {
    String resolveUri(String path);

    Duration timeout();

    String accept();

    String contentType();

    boolean acceptGZip();

    Class<?> errorType();
}
