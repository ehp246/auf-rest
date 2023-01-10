package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ehp246.aufrest.api.exception.BadGatewayException;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HttpClientBuilderSupplier;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor;
import me.ehp246.aufrest.core.rest.AufRestConfiguration;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;

/**
 * For each call for a HTTP client, the provider should ask the client-builder
 * supplier for a new builder.
 * <p>
 * For each HTTP request, the provider should ask the request-builder supplier
 * for a new builder.
 * <p>
 * The provider should not cache/re-use any builders.
 * <p>
 * Internal Spring bean.
 *
 * @author Lei Yang
 * @since 1.0
 * @version 4.0
 * @see {@link RestFnProvider}, {@link RestFn},
 *      {@linkplain AufRestConfiguration}
 */
public final class DefaultRestFnProvider implements RestFnProvider {
    private final HttpClientBuilderSupplier clientBuilderSupplier;
    private final HttpRequestBuilder reqBuilder;
    private final List<RestListener> listeners;
    private final InferringBodyHandlerProvider handlerProvider;
    private final RestLogger restLogger;

    public DefaultRestFnProvider(final HttpRequestBuilder reqBuilder,
            final InferringBodyHandlerProvider handlerProvider, final HttpClientBuilderSupplier clientBuilderSupplier,
            final List<RestListener> listeners, final RestLogger restLogger) {
        this.clientBuilderSupplier = clientBuilderSupplier == null ? HttpClient::newBuilder : clientBuilderSupplier;
        this.reqBuilder = Objects.requireNonNull(reqBuilder,
                HttpRequestBuilder.class.getSimpleName() + " must be specified");
        this.listeners = listeners == null ? List.of() : Collections.unmodifiableList(listeners);
        this.restLogger = restLogger;
        this.handlerProvider = Objects.requireNonNull(handlerProvider,
                InferringBodyHandlerProvider.class.getSimpleName() + " must be specified");
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

            @SuppressWarnings("unchecked")
            @Override
            public <T> HttpResponse<T> applyForResponse(final RestRequest req,
                    final RestBodyDescriptor<?> requestBodyDescriptor,
                    final RestResponseDescriptor<T> responseBodyDescriptor) {
                final var httpReq = reqBuilder.apply(req, requestBodyDescriptor);

                listeners.stream().forEach(listener -> listener.onRequest(httpReq, req));

                if (restLogger != null) {
                    restLogger.onRequest(httpReq, req);
                }

                final var handler = responseBodyDescriptor instanceof final RestResponseDescriptor.Provided<T> handlerSupplier
                        ? handlerSupplier.handler()
                        : handlerProvider.get(responseBodyDescriptor);

                final HttpResponse<?> httpResponse;
                // Try/catch on send only.
                try {
                    httpResponse = client.send(httpReq, handler);

                    if (!HttpUtils.isSuccess(httpResponse.statusCode())) {
                        // Should throw the more specific type if possible.
                        final var statusCode = httpResponse.statusCode();
                        if (statusCode >= 600) {
                            throw new ErrorResponseException(req, httpResponse);
                        } else if (statusCode == 500) {
                            throw new InternalServerErrorException(req, httpResponse);
                        } else if (statusCode == 502) {
                            throw new BadGatewayException(req, httpResponse);
                        } else if (statusCode == 503) {
                            throw new ServiceUnavailableException(req, httpResponse);
                        } else if (statusCode == 504) {
                            throw new GatewayTimeoutException(req, httpResponse);
                        } else if (statusCode >= 500) {
                            throw new ServerErrorResponseException(req, httpResponse);
                        } else if (statusCode >= 400) {
                            throw new ClientErrorResponseException(req, httpResponse);
                        }

                        throw new RedirectionResponseException(req, httpResponse);
                    }
                } catch (IOException | InterruptedException | ErrorResponseException e) {
                    LOGGER.atTrace().withThrowable(e).log("Request failed: {} ", e::getMessage);

                    listeners.stream().forEach(listener -> listener.onException(e, httpReq, req));

                    if (e instanceof final ErrorResponseException error) {
                        throw new UnhandledResponseException(error);
                    }
                    /*
                     * Wrap only the checked.
                     */
                    throw new RestFnException(e);
                }

                listeners.stream().forEach(listener -> listener.onResponse(httpResponse, req));

                return (HttpResponse<T>) httpResponse;
            }
        };
    }
}
