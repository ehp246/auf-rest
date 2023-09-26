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
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Helper bean for the convenience of the application.
 *
 * @author Lei Yang
 *
 */
public final class RestLogger {
    private final static Logger LOGGER = LogManager.getLogger(RestLogger.class);

    private final static List<String> MASKED = List.of("*");
    private final static Marker REQUEST = MarkerManager.getMarker("REQUEST");
    private final static Marker REQUEST_HEADERS = MarkerManager.getMarker("REQUEST_HEADERS");
    private final static Marker REQUEST_BODY = MarkerManager.getMarker("REQUEST_BODY");

    private final static Marker RESPONSE = MarkerManager.getMarker("RESPONSE");
    private final static Marker RESPONSE_HEADERS = MarkerManager.getMarker("RESPONSE_HEADERS");
    private final static Marker RESPONSE_BODY = MarkerManager.getMarker("RESPONSE_BODY");

    private final static Subscriber<ByteBuffer> REQUEST_BODY_SUBSCRIBER = new Subscriber<>() {

        @Override
        public void onSubscribe(final Subscription subscription) {
            subscription.request(1);
        }

        @Override
        public void onNext(final ByteBuffer item) {
            LOGGER.atTrace().withMarker(REQUEST_BODY).log("{}", () -> new String(item.array(), StandardCharsets.UTF_8));
        }

        @Override
        public void onError(final Throwable throwable) {
            LOGGER.atTrace().withMarker(REQUEST_BODY).withThrowable(throwable).log("Failed to log body: {}",
                    throwable::getMessage);
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
        LOGGER.atInfo().withMarker(REQUEST).log("{}", () -> httpRequest.method() + " " + httpRequest.uri());

        LOGGER.atDebug().withMarker(REQUEST_HEADERS).log("{}", () -> maskHeaders(httpRequest.headers().map()));

        final var body = req.body();

        if (body instanceof BodyPublisher || body instanceof InputStream || body instanceof Path) {
            LOGGER.atTrace().withMarker(REQUEST_BODY).log("");
            return;
        }

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(REQUEST_BODY_SUBSCRIBER),
                () -> LOGGER.atTrace().withMarker(REQUEST_BODY).log(""));
    }

    public void onResponseInfo(final HttpResponse.ResponseInfo responseInfo) {
        LOGGER.atInfo().withMarker(RESPONSE).log("{}", responseInfo::statusCode);

        LOGGER.atDebug().withMarker(RESPONSE_HEADERS).log("{}", () -> maskHeaders(responseInfo.headers().map()));
    }

    public void onResponseBody(final String text) {
        LOGGER.atTrace().withMarker(RESPONSE_BODY).log(text);
    }

    private String maskHeaders(final Map<String, List<String>> headers) {
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
