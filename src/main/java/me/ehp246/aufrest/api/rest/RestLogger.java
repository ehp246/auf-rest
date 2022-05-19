package me.ehp246.aufrest.api.rest;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.spi.ToJson;

/**
 * Helper bean for the convenience of the application.
 * 
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
            LOGGER.atTrace().log("{}", () -> new String(item.array(), StandardCharsets.UTF_8));
        }

        @Override
        public void onError(final Throwable throwable) {
            LOGGER.atError().withThrowable(throwable).log("Failed to log body: {}", throwable::getMessage);
        }

        @Override
        public void onComplete() {
        }
    };

    private final ToJson toJson;

    public RestLogger(final ToJson toJson) {
        super();
        this.toJson = toJson;
    }

    @Override
    public void onRequest(final HttpRequest httpRequest, final RestRequest request) {
        LOGGER.atInfo().log(httpRequest.method() + " " + httpRequest.uri());

        LOGGER.atDebug().log(maskHeaders(httpRequest.headers().map()));

        // Logging body only on TRACE.
        if (request.body() instanceof InputStream) {
            LOGGER.atTrace().log(request.body().toString());
            return;
        }

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(subscriber), () -> LOGGER.atTrace().log("-"));
    }

    @Override
    public void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
        LOGGER.atInfo().log(httpResponse.statusCode());

        LOGGER.atDebug().log(maskHeaders(httpResponse.headers().map()));

        // Logging response body only on TRACE.
        try {
            LOGGER.atTrace().log("{}", () -> this.toJson.apply(httpResponse.body()));
        } catch (Exception e) {
            LOGGER.atWarn().withThrowable(e).log("Failed to log response body: {}", e::getMessage);
        }
    }

    @Override
    public void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
        LOGGER.atInfo().withThrowable(exception).log("Request failed: {}", exception::getMessage);
    }

    private static Map<String, List<String>> maskHeaders(Map<String, List<String>> headers) {
        final var masked = new HashMap<>(headers);

        masked.computeIfPresent(HttpUtils.AUTHORIZATION, (key, values) -> List.of("*"));

        return masked;
    }
}
