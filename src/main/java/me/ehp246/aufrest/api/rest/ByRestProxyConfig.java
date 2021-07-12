package me.ehp246.aufrest.api.rest;

import java.time.Duration;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * The instantiation of a {@link ByRest} annotation as an object.
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
}
