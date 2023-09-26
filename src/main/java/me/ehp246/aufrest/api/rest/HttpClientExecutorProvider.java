package me.ehp246.aufrest.api.rest;

import java.util.concurrent.Executor;

/**
 * @author Lei Yang
 * @since 4.1.0
 */
@FunctionalInterface
public interface HttpClientExecutorProvider {
    Executor get(Config config);

    record Config(String name) {
    }
}
