package me.ehp246.aufrest.core.rest;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.ehp246.aufrest.api.rest.HttpClientExecutorProvider;

/**
 * @author Lei Yang
 *
 */
final class DefaultHttpClientExecutorProvider implements HttpClientExecutorProvider {

    @Override
    public Executor get(final Config config) {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
                .name("HttpClient-" + Objects.requireNonNull(config.name()) + "-Worker-", 0)
                .factory());
    }
}
