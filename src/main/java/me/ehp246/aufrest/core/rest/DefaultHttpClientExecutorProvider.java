package me.ehp246.aufrest.core.rest;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import me.ehp246.aufrest.api.rest.HttpClientExecutorProvider;

/**
 * @author Lei Yang
 *
 */
class DefaultHttpClientExecutorProvider implements HttpClientExecutorProvider {

    @Override
    public Executor get(final Config config) {
        return Executors.newCachedThreadPool(new ThreadFactory() {
            private final String namePrefix = "HttpClient-" + Objects.requireNonNull(config.name()) + "-Worker-";
            private final AtomicInteger nextId = new AtomicInteger();

            @Override
            public Thread newThread(final Runnable runnable) {
                final var thread = new Thread(null, runnable, namePrefix + nextId.getAndIncrement(), 0, false);
                thread.setDaemon(true);
                return thread;
            }
        });
    }
}
