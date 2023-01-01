package me.ehp246.aufrest.api.rest;

import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.spi.DeclarationDescriptor.ReifyingBodyDescriptor;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface JsonBodyHandlerProvider {
    BodyHandler<?> get(ReifyingBodyDescriptor descriptor);
}
