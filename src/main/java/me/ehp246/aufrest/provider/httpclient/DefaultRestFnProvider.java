package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.ehp246.aufrest.api.exception.BadGatewayException;
import me.ehp246.aufrest.api.exception.BadRequestException;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.ForbiddenException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.NotAcceptableException;
import me.ehp246.aufrest.api.exception.NotAllowedException;
import me.ehp246.aufrest.api.exception.NotAuthorizedException;
import me.ehp246.aufrest.api.exception.NotFoundException;
import me.ehp246.aufrest.api.exception.NotSupportedException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.RestFnException;
import me.ehp246.aufrest.api.exception.ServerErrorException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BodyHandlerType;
import me.ehp246.aufrest.api.rest.BodyOf;
import me.ehp246.aufrest.api.rest.RestFnConfig;
import me.ehp246.aufrest.api.rest.HttpClientBuilderSupplier;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.Log4jContext;
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
 * @see RestFnProvider
 * @see RestFn
 * @see AufRestConfiguration
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
    public RestFn get(final RestFnConfig restFnConfig) {
        return new RestFn() {
            private final HttpClient client = clientBuilderSupplier.get().build();

            @SuppressWarnings("unchecked")
            @Override
            public <T> HttpResponse<T> applyForResponse(final RestRequest req, final BodyOf<?> requestBodyDescriptor,
                    final BodyHandlerType<T> responseBodyDescriptor) {

                Log4jContext.set(req);

                final var httpReq = reqBuilder.apply(req, requestBodyDescriptor);

                listeners.stream().forEach(listener -> listener.onRequest(httpReq, req));

                if (restLogger != null) {
                    restLogger.onRequest(httpReq, req);
                }

                final var handler = responseBodyDescriptor instanceof final BodyHandlerType.Provided<T> handlerSupplier
                        ? handlerSupplier.handler()
                        : handlerProvider.get(responseBodyDescriptor);

                final HttpResponse<?> httpResponse;
                // Try/catch on send only.
                try {
                    httpResponse = client.send(httpReq, Log4jContext.wrap(req, handler));

                    listeners.stream().forEach(listener -> listener.onResponse(httpResponse, req));

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
                        } else if (statusCode > 500) {
                            throw new ServerErrorException(req, httpResponse);
                        } else if (statusCode == 400) {
                            throw new BadRequestException(req, httpResponse);
                        } else if (statusCode == 401) {
                            throw new NotAuthorizedException(req, httpResponse);
                        } else if (statusCode == 403) {
                            throw new ForbiddenException(req, httpResponse);
                        } else if (statusCode == 404) {
                            throw new NotFoundException(req, httpResponse);
                        } else if (statusCode == 405) {
                            throw new NotAllowedException(req, httpResponse);
                        } else if (statusCode == 406) {
                            throw new NotAcceptableException(req, httpResponse);
                        } else if (statusCode == 415) {
                            throw new NotSupportedException(req, httpResponse);
                        } else if (statusCode > 400) {
                            throw new ClientErrorException(req, httpResponse);
                        }

                        throw new RedirectionException(req, httpResponse);
                    }
                } catch (IOException | InterruptedException | ErrorResponseException e) {
                    if (e instanceof final ErrorResponseException error) {
                        throw new UnhandledResponseException(error);
                    } else {
                        try {
                            listeners.stream().forEach(listener -> listener.onException(e, httpReq, req));
                        } catch (Exception le) {
                            e.addSuppressed(le);
                        }
                    }
                    /*
                     * Wrap only the checked.
                     */
                    throw new RestFnException(e, httpReq, req);
                } finally {
                    Log4jContext.clear(req);
                }

                Log4jContext.clear(req);

                return (HttpResponse<T>) httpResponse;
            }
        };
    }
}
