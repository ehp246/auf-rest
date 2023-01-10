package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient;
import java.util.function.Supplier;

/**
 * @author Lei Yang
 * @since 4.0
 */
@FunctionalInterface
public interface HttpClientBuilderSupplier extends Supplier<HttpClient.Builder> {
}
