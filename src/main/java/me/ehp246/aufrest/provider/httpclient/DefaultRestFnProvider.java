package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.rest.BodyDescriptor;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HttpRequestBuilder;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * For each call for a HTTP client, the provider should ask the client-builder
 * supplier for a new builder. For each HTTP request, the provider should ask
 * the request-builder supplier for a new builder. The provider should not
 * cache/re-use any builders.
 *
 * @author Lei Yang
 */
public final class DefaultRestFnProvider implements RestFnProvider {
    private final Supplier<HttpClient.Builder> clientBuilderSupplier;
    private final HttpRequestBuilder reqBuilder;
    private final List<RestListener> listeners;
    private final RestLogger restLogger;

    public DefaultRestFnProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier) {
        this(clientBuilderSupplier, (req, descriptor) -> null, null);
    }

    @Autowired
    public DefaultRestFnProvider(final HttpRequestBuilder reqBuilder, final List<RestListener> listeners,
            @Nullable final RestLogger restLogger) {
        this.clientBuilderSupplier = HttpClient::newBuilder;
        this.reqBuilder = reqBuilder;
        this.listeners = listeners;
        this.restLogger = restLogger;
    }

    public DefaultRestFnProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier,
            final HttpRequestBuilder restToHttp, final List<RestListener> listeners) {
        this.clientBuilderSupplier = clientBuilderSupplier;
        this.reqBuilder = restToHttp;
        this.listeners = listeners == null ? List.of() : new ArrayList<>(listeners);
        this.restLogger = null;
    }

    @Override
    public RestFn get(final ClientConfig clientConfig) {
        final var clientBuilder = clientBuilderSupplier.get();
        if (clientConfig.connectTimeout() != null) {
            clientBuilder.connectTimeout(clientConfig.connectTimeout());
        }

        return new RestFn() {
            private static final Logger LOGGER = LogManager.getLogger(RestFn.class);

            private final HttpClient client = clientBuilder.build();

            @Override
            public HttpResponse<?> apply(final RestRequest req, final BodyDescriptor descriptor,
                    final ResponseConsumer consumer) {
                final var httpReq = reqBuilder.apply(req, descriptor);

                listeners.stream().forEach(listener -> listener.onRequest(httpReq, req));

                if (restLogger != null) {
                    restLogger.onRequest(httpReq, req);
                }

                final HttpResponse<?> httpResponse;
                // Try/catch on send only.
                try {
                    httpResponse = client.send(httpReq, consumer.handler());
                } catch (IOException | InterruptedException e) {
                    LOGGER.atTrace().withThrowable(e).log("Request failed: {} ", e::getMessage);

                    listeners.stream().forEach(listener -> listener.onException(e, httpReq, req));

                    throw new RestFnException(e);
                }

                listeners.stream().forEach(listener -> listener.onResponse(httpResponse, req));

                return httpResponse;
            }
        };
    }
}
