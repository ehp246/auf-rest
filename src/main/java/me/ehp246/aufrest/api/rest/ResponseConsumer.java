package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

/**
 * @author Lei Yang
 *
 */
public interface ResponseConsumer {
    BodyHandler<?> handler();
}
