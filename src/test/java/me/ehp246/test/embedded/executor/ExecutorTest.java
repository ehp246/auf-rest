package me.ehp246.test.embedded.executor;

import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class ExecutorTest {
    @Autowired
    private ExecutorCase testCase;

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var future = new CompletableFuture<Thread>();
        testCase.get(new BodyHandler<String>() {

            @Override
            public BodySubscriber<String> apply(final ResponseInfo responseInfo) {
                future.complete(Thread.currentThread());
                return BodySubscribers.ofString(StandardCharsets.UTF_8);
            }
        });

        Assertions.assertEquals(true, future.get().getName().contains("ExecutorCase"));
    }

}
