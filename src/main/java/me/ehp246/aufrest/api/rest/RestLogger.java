package me.ehp246.aufrest.api.rest;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

/**
 * Helper bean for the convenience of the application.
 * 
 * @author Lei Yang
 *
 */
public final class RestLogger {
    private final static Logger LOGGER = LogManager.getLogger(RestLogger.class);
    private final static List<String> MASKED = List.of("*");

    private final static Subscriber<ByteBuffer> SUBSCRIBER = new Subscriber<>() {

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
            LOGGER.atTrace().withThrowable(throwable).log("Failed to log body: {}", throwable::getMessage);
        }

        @Override
        public void onComplete() {
        }
    };

    private final Set<String> maskedHeaders = new HashSet<>();

    public RestLogger(final Set<String> maskedHeaders) {
        super();
        if (maskedHeaders != null) {
            maskedHeaders.stream().forEach(name -> this.maskedHeaders.add(name.toLowerCase(Locale.US)));
        }
    }

    public void onRequest(final HttpRequest httpRequest, final RestRequest req) {
        LOGGER.atInfo().log("{}", () -> httpRequest.method() + " " + httpRequest.uri());

        final var headers = httpRequest.headers().map();

        LOGGER.atDebug().log("{}", () -> maskHeaders(headers));

        final var body = req.body();

        if (body instanceof BodyPublisher || body instanceof InputStream || body instanceof Path) {
            LOGGER.atTrace().log("");
            return;
        }

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(SUBSCRIBER), () -> LOGGER.atTrace().log(""));
    }

    public void onResponseInfo(HttpResponse.ResponseInfo responseInfo) {
        LOGGER.atInfo().log("{}", responseInfo::statusCode);

        LOGGER.atDebug().log("{}", () -> maskHeaders(responseInfo.headers().map()));
    }

    public void onResponseBody(String text) {
        LOGGER.atTrace().log(text);
    }

    private String maskHeaders(Map<String, List<String>> headers) {
        final var workingMap = new HashMap<>(headers.size());
        
        headers.entrySet().forEach(entry -> {
            final var key = entry.getKey();
            if (this.maskedHeaders.contains(key.toLowerCase(Locale.US))) {
                workingMap.put(key, MASKED);
            } else {
                workingMap.put(key, entry.getValue());
            }
        });

        return workingMap.toString();
    }
}
