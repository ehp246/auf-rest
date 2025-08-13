package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.util.function.Supplier;

import me.ehp246.aufrest.core.rest.AufRestConfiguration;

/**
 * @author Lei Yang
 * @since 4.0
 * @see AufRestConfiguration#httpClientBuilderSupplier(String)
 */
@FunctionalInterface
public interface HttpClientBuilderSupplier extends Supplier<HttpClient.Builder> {
}
