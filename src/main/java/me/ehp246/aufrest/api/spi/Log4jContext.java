package me.ehp246.aufrest.api.spi;

import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
public final class Log4jContext {
    private Log4jContext() {
    }

    private enum ContextName {
        AufRestRequestId;
    }

    public static <T> BodyHandler<T> wrap(final RestRequest req, final BodyHandler<T> handler) {
        return new BodyHandler<T>() {
            @Override
            public BodySubscriber<T> apply(final ResponseInfo responseInfo) {
                Log4jContext.set(req);

                final var target = handler.apply(responseInfo);

                return new BodySubscriber<T>() {

                    @Override
                    public void onSubscribe(final Subscription subscription) {
                        target.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(final List<ByteBuffer> item) {
                        target.onNext(item);
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        target.onError(throwable);

                        Log4jContext.clear(req);
                    }

                    @Override
                    public void onComplete() {
                        target.onComplete();

                        Log4jContext.clear(req);
                    }

                    @Override
                    public CompletionStage<T> getBody() {
                        return target.getBody();
                    }
                };
            }
        };
    }

    public static void set(final RestRequest req) {
        if (req == null) {
            return;
        }

        ThreadContext.put(ContextName.AufRestRequestId.name(), req.id());
    }

    public static void clear(final RestRequest req) {
        ThreadContext.remove(ContextName.AufRestRequestId.name());
    }
}
