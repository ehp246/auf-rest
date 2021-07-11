package me.ehp246.aufrest.api.configuration;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BodyPublisherProvider;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InvocationAuthProvider;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestFnProvider;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.spi.InvocationAuthProviderResolver;
import me.ehp246.aufrest.api.spi.PropertyResolver;
import me.ehp246.aufrest.core.util.OneUtil;
import me.ehp246.aufrest.provider.httpclient.DefaultRequestBuilder;
import me.ehp246.aufrest.provider.httpclient.DefaultRestFnProvider;
import me.ehp246.aufrest.provider.jackson.JsonByJackson;

/**
 * Registers infrastructure beans needed by the framework.
 *
 * <p>
 * Imported by {@link me.ehp246.aufrest.api.annotation.EnableByRest
 * EnableByRest}.
 *
 * @author Lei Yang
 * @see me.ehp246.aufrest.api.annotation.EnableByRest
 * @since 1.0
 */
@Import({ DefaultRestFnProvider.class })
public final class ByRestConfiguration {
    @Bean("8d4bb36b-67e6-4af9-8d27-c69ed217e235")
    public RestClientConfig restClientConfig(
            @Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout,
            @Autowired(required = false) final BodyHandlerProvider bodyHandlerProvider) {
        final var connTimeout = Optional.ofNullable(connectTimeout).filter(OneUtil::hasValue)
                .map(value -> OneUtil.orThrow(() -> Duration.parse(value),
                        e -> new IllegalArgumentException("Invalid Connection Timeout: " + value)))
                .orElse(null);

        return new RestClientConfig() {

            @Override
            public Duration connectTimeout() {
                return connTimeout;
            }

            /**
             * Default to discarding.
             */
            @Override
            public BodyHandlerProvider bodyHandlerProvider() {
                return bodyHandlerProvider != null ? bodyHandlerProvider
                        : req -> respInfo -> BodySubscribers.mapping(BodySubscribers.discarding(), body -> null);
            }

        };
    }

    @Bean("ff1e0d94-2413-4d4c-8822-411641137fdd")
    public JsonByJackson jacksonFn(final ObjectMapper objectMapper) {
        return new JsonByJackson(objectMapper);
    }

    @Bean("063d7d99-ac10-4746-a308-390bad7872e2")
    public BodyPublisherProvider bodyPublisherProvider(final JsonByJackson jacksonFn) {
        return req -> {
            // No content type, no content.
            if (req.body() == null || !OneUtil.hasValue(req.contentType())) {
                return BodyPublishers.noBody();
            }
            if (req.contentType().toLowerCase().startsWith(HttpUtils.TEXT_PLAIN)) {
                return BodyPublishers.ofString(req.body().toString());
            }

            // Default to JSON.
            return BodyPublishers.ofString(jacksonFn.toJson(req.body()));
        };
    }

    @Bean("c1af17fc-0e88-4d4a-a5ce-648aea1adb17")
    public BodyHandlerProvider bodyHandlerProvider(final JsonByJackson jacksonFn) {
        return req -> {
            final var receiver = req.bodyReceiver();
            final Class<?> type = receiver == null ? void.class : receiver.type();

            if (type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class)) {
                return BodyHandlers.discarding();
            }

            if (type.isAssignableFrom(String.class)) {
                return BodyHandlers.ofString();
            }

            // Declared return type requires de-serialization.
            return responseInfo -> {
                final var encoding = responseInfo.headers().firstValue(HttpHeaders.CONTENT_ENCODING).orElse("");

                return BodySubscribers.mapping(BodySubscribers.ofInputStream(), bodyIs -> {
                    final String text;
                    try (final var is = encoding.equalsIgnoreCase("gzip") ? new GZIPInputStream(bodyIs) : new BufferedInputStream(bodyIs);
                            final var byteOs = new ByteArrayOutputStream()) {
                        is.transferTo(byteOs);
                        text = byteOs.toString(StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (responseInfo.statusCode() == 204) {
                        return null;
                    }

                    if (responseInfo.statusCode() >= 300) {
                        return text;
                    }

                    // The server might not set the header. Assuming JSON.
                    final var contentType = responseInfo.headers().firstValue(HttpHeaders.CONTENT_TYPE)
                            .orElse(MediaType.APPLICATION_JSON_VALUE).toLowerCase();
                    if (contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
                        return jacksonFn.fromJson(text, receiver);
                    }
                    return text;
                });
            };
        };
    }

    @Bean("3eddc6a6-f990-4f41-b6e5-2ae1f931dde7")
    public RestLogger restLogger(final ObjectMapper objectMapper) {
        return new RestLogger(objectMapper);
    }

    @Bean("55b212a8-2783-4a46-aa5d-60ceb4b2c0d9")
    public PropertyResolver propertyResolver(final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }

    @Bean("baa8af0b-4da4-487f-a686-3d1e8387dbb6")
    public RequestBuilder requestBuilder(@Autowired(required = false) final HeaderProvider headerProvider,
            @Autowired(required = false) final AuthProvider authProvider,
            @Autowired(required = false) final BodyPublisherProvider bodyPublisherProvider,
            @Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout) {
        return new DefaultRequestBuilder(HttpRequest::newBuilder, headerProvider, authProvider, bodyPublisherProvider,
                requestTimeout);
    }

    @Bean("8a7808c6-d088-42e5-a504-ab3dad149e1d")
    public InvocationAuthProviderResolver methodAuthProviderMap(final BeanFactory env) {
        return name -> env.getBean(name, InvocationAuthProvider.class);
    }

    @Bean("ac6621d6-1220-4248-ba3f-29f9dc54499b")
    public RestFn restFn(final RestFnProvider restFnProvider, final RestClientConfig clientConfig) {
        return restFnProvider.get(clientConfig);
    }
}
