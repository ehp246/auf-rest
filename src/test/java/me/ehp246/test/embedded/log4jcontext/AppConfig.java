package me.ehp246.test.embedded.log4jcontext;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import(Jackson.class)
class AppConfig {
    private final AtomicReference<CompletableFuture<Map<String, String>>> responseContextRef = new AtomicReference<>(
            new CompletableFuture<>());
    private final AtomicReference<CompletableFuture<Map<String, String>>> requestContextRef = new AtomicReference<>(
            new CompletableFuture<>());
    private final AtomicReference<CompletableFuture<RestRequest>> requestRef = new AtomicReference<>(
            new CompletableFuture<>());

    void reset() {
        responseContextRef.set(new CompletableFuture<Map<String, String>>());
        requestContextRef.set(new CompletableFuture<Map<String, String>>());
        requestRef.set(new CompletableFuture<RestRequest>());
    }

    Map<String, String> takeResponseContextMap() throws InterruptedException, ExecutionException {
        final var map = responseContextRef.get().get();

        responseContextRef.set(new CompletableFuture<>());

        return map;
    }

    Map<String, String> takeRequestContextMap() throws InterruptedException, ExecutionException {
        return requestContextRef.get().get();
    }

    RestRequest takeRequest() throws InterruptedException, ExecutionException {
        return requestRef.get().get();
    }

    @Bean
    HttpResponse.BodyHandler<Void> responseHandler1() {
        return responseInfo -> {
            responseContextRef.get().complete(ThreadContext.getContext());
            return HttpResponse.BodySubscribers.discarding();
        };
    }

    @Bean
    RestListener restListener() {
        return new RestListener() {

            @Override
            public void onRequest(final HttpRequest httpRequest, final RestRequest restRequest) {
                requestRef.get().complete(restRequest);
                requestContextRef.get().complete(ThreadContext.getImmutableContext());
            }
        };
    }
}
