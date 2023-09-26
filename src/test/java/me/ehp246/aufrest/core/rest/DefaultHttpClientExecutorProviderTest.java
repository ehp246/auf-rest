package me.ehp246.aufrest.core.rest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.HttpClientExecutorProvider.Config;

/**
 * @author Lei Yang
 *
 */
class DefaultHttpClientExecutorProviderTest {

    @Test
    void test_01() {
        final var provider = new DefaultHttpClientExecutorProvider();

        Assertions.assertThrows(NullPointerException.class, () -> provider.get(null));
        Assertions.assertThrows(NullPointerException.class, () -> provider.get(new Config(null)));
        Assertions.assertNotNull(provider.get(new Config("")));
    }

    @Test
    void test_02() throws InterruptedException, ExecutionException {
        final var expected = UUID.randomUUID().toString();
        final var future = new CompletableFuture<Thread>();
        new DefaultHttpClientExecutorProvider().get(new Config(expected))
                .execute(() -> future.complete(Thread.currentThread()));

        Assertions.assertEquals(true, future.get().getName().contains(expected), "should have the name");
        Assertions.assertEquals(true, future.get().isDaemon());
    }
}
