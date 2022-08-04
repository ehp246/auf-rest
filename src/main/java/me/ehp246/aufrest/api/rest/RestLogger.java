package me.ehp246.aufrest.api.rest;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
    private final static List<String> MASKED = List.of("*");

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
    private final Set<String> maskedHeaders = new HashSet<>();
    private final Map<String, List<String>> workingMap = new HashMap<>();

    public RestLogger(final ToJson toJson, final Set<String> maskedHeaders) {
        super();
        this.toJson = toJson;
        if (maskedHeaders != null) {
            maskedHeaders.stream().forEach(name -> this.maskedHeaders.add(name.toLowerCase(Locale.US)));
        }
    }

    @Override
    public void onRequest(final HttpRequest httpRequest, final RestRequest request) {
        LOGGER.atInfo().log("{}", () -> httpRequest.method() + " " + httpRequest.uri());

        LOGGER.atDebug().log("{}", () -> maskHeaders(httpRequest.headers().map()));

        // Logging body only on TRACE.
        if (request.body() instanceof InputStream is) {
            LOGGER.atTrace().log("{}", () -> is.toString());
            return;
        }

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(subscriber), () -> LOGGER.atTrace().log("-"));
    }

    @Override
    public void onResponse(HttpResponse<?> httpResponse, RestRequest req) {
        LOGGER.atInfo().log("{}", httpResponse::statusCode);

        LOGGER.atDebug().log("{}", () -> maskHeaders(httpResponse.headers().map()));

        // Logging response body only on TRACE.
        try {
            LOGGER.atTrace().log("{}", () -> this.toJson.apply(httpResponse.body()));
        } catch (Exception e) {
            LOGGER.atTrace().withThrowable(e).log("Failed to log response body: {}", e::getMessage);
        }
    }

    @Override
    public void onException(Exception exception, HttpRequest httpRequest, RestRequest req) {
        LOGGER.atInfo().withThrowable(exception).log("Request failed: {}", exception::getMessage);
    }

    private String maskHeaders(Map<String, List<String>> headers) {
        this.workingMap.clear();
        
        headers.entrySet().forEach(entry -> {
            final var key = entry.getKey();
            if (this.maskedHeaders.contains(key.toLowerCase(Locale.US))) {
                this.workingMap.put(key, MASKED);
            } else {
                this.workingMap.put(key, entry.getValue());
            }
        });

        final var str = this.workingMap.toString();
        this.workingMap.clear();

        return str;
    }
}
