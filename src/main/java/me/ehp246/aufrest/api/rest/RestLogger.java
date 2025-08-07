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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Helper bean for the convenience of the application.
 *
 * @author Lei Yang
 *
 */
public final class RestLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestLogger.class);

    private static final List<String> MASKED = List.of("*");
    private static final Marker REQUEST = MarkerFactory.getMarker("REQUEST");
    private static final Marker REQUEST_HEADERS = MarkerFactory.getMarker("REQUEST_HEADERS");
    private static final Marker REQUEST_BODY = MarkerFactory.getMarker("REQUEST_BODY");

    private static final Marker RESPONSE = MarkerFactory.getMarker("RESPONSE");
    private static final Marker RESPONSE_HEADERS = MarkerFactory.getMarker("RESPONSE_HEADERS");
    private static final Marker RESPONSE_BODY = MarkerFactory.getMarker("RESPONSE_BODY");

    private static final Subscriber<ByteBuffer> REQUEST_BODY_SUBSCRIBER = new Subscriber<>() {

        @Override
        public void onSubscribe(final Subscription subscription) {
            subscription.request(1);
        }

        @Override
        public void onNext(final ByteBuffer item) {
            LOGGER.atTrace().addMarker(REQUEST_BODY).setMessage("{}")
                    .addArgument(() -> new String(item.array(), StandardCharsets.UTF_8)).log();
        }

        @Override
        public void onError(final Throwable throwable) {
            LOGGER.atTrace().addMarker(REQUEST_BODY).setCause(throwable).setMessage("Failed to log body: {}")
                    .addArgument(throwable::getMessage).log();
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
        LOGGER.atInfo().addMarker(REQUEST).setMessage("{} {}").addArgument(httpRequest::method)
                .addArgument(httpRequest::uri).log();

        LOGGER.atDebug().addMarker(REQUEST_HEADERS).setMessage("{}")
                .addArgument(() -> maskHeaders(httpRequest.headers().map())).log();

        final var body = req.body();

        if (body instanceof BodyPublisher || body instanceof InputStream || body instanceof Path) {
            LOGGER.atTrace().addMarker(REQUEST_BODY).log("");
            return;
        }

        httpRequest.bodyPublisher().ifPresentOrElse(pub -> pub.subscribe(REQUEST_BODY_SUBSCRIBER),
                () -> LOGGER.atTrace().addMarker(REQUEST_BODY).log(""));
    }

    public void onResponseInfo(final HttpResponse.ResponseInfo responseInfo) {
        LOGGER.atInfo().addMarker(RESPONSE).setMessage("{}").addArgument(responseInfo::statusCode).log();

        LOGGER.atDebug().addMarker(RESPONSE_HEADERS).setMessage("{}")
                .addArgument(() -> maskHeaders(responseInfo.headers().map())).log();
    }

    public void onResponseBody(final String text) {
        LOGGER.atTrace().addMarker(RESPONSE_BODY).log(text);
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
