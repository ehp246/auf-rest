package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class RestLogger implements RestListener {
    private final static Logger LOGGER = LogManager.getLogger(RestLogger.class);
    private final static Subscriber<ByteBuffer> subscriber = new Subscriber<>() {

        @Override
        public void onSubscribe(final Subscription subscription) {
            subscription.request(1);
        }

        @Override
        public void onNext(final ByteBuffer item) {
            LOGGER.atTrace().log(new String(item.array(), StandardCharsets.UTF_8));
        }

        @Override
        public void onError(final Throwable throwable) {
            LOGGER.atError().log(throwable);
        }

        @Override
        public void onComplete() {
        }
    };

    private final ObjectMapper objectMapper;

    public RestLogger(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public void onRequest(final HttpRequest httpRequest, final RestRequest request) {
        LOGGER.atTrace().log(httpRequest.method() + " " + httpRequest.uri());
        LOGGER.atTrace().log(httpRequest.headers().map());

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(subscriber), () -> LOGGER.atTrace().log("-"));
    }

    @Override
    public void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
        LOGGER.atTrace().log(httpResponse.request().method() + " " + httpResponse.uri().toString() + " "
                + httpResponse.statusCode());

        LOGGER.atTrace().log(httpResponse.headers().map());
        LOGGER.atTrace().log(OneUtil.orThrow(() -> this.objectMapper.writeValueAsString(httpResponse.body())));
    }
}
