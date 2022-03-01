package me.ehp246.aufrest.api.rest;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        if (!LOGGER.isTraceEnabled()) {
            return;
        }

        LOGGER.trace(httpRequest.method() + " " + httpRequest.uri());
        LOGGER.trace(httpRequest.headers().map());

        // Special support for InputStream
        if (request.body() != null && request.body() instanceof InputStream) {
            LOGGER.trace(request.body());
            return;
        }

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(subscriber), () -> LOGGER.trace("-"));
    }

    @Override
    public void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
        if (!LOGGER.isTraceEnabled()) {
            return;
        }

        LOGGER.trace(httpResponse.statusCode());

        LOGGER.trace(httpResponse.headers().map());
        try {
            LOGGER.trace(this.objectMapper.writeValueAsString(httpResponse.body()));
        } catch (Exception e) {
            LOGGER.atWarn().log("Failed to log response body: " + httpResponse.body());
        }
    }
}
