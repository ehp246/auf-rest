package me.ehp246.test.embedded.log4jcontext;

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
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import(Jackson.class)
class AppConfig {
    private final AtomicReference<CompletableFuture<Map<String, String>>> ref = new AtomicReference<>(
            new CompletableFuture<>());

    Map<String, String> takeContextMap() throws InterruptedException, ExecutionException {
        final var map = ref.get().get();

        ref.set(new CompletableFuture<>());

        return map;
    }

    @Bean
    HttpResponse.BodyHandler<Void> responseHandler1() {
        return responseInfo -> {
            ref.get().complete(ThreadContext.getContext());
            return HttpResponse.BodySubscribers.discarding();
        };
    }
}
