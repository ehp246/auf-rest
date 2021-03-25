package me.ehp246.aufrest.api.configuration;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BodyPublisherProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestConsumer;
import me.ehp246.aufrest.core.util.OneUtil;
import me.ehp246.aufrest.provider.httpclient.JdkRestFnProvider;
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
@Import(JdkRestFnProvider.class)
public final class ByRestConfiguration {

	public ClientConfig clientConfig(final String connectTimeout, final String requestTimeout) {
		final var connTimeout = Optional.ofNullable(connectTimeout).filter(OneUtil::hasValue)
				.map(value -> OneUtil.orThrow(() -> Duration.parse(value),
						e -> new IllegalArgumentException("Invalid Connection Timeout: " + value)))
				.orElse(null);

		final var responseTimeout = Optional.ofNullable(requestTimeout).filter(OneUtil::hasValue)
				.map(value -> OneUtil.orThrow(() -> Duration.parse(value),
						e -> new IllegalArgumentException("Invalid Response Timeout: " + value)))
				.orElse(null);
		return new ClientConfig() {
			@Override
			public Duration connectTimeout() {
				return connTimeout;
			}

			@Override
			public Duration responseTimeout() {
				return responseTimeout;
			}
		};
	}

	@Bean
	public ClientConfig clientConfig(@Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout,
			@Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout,
			@Autowired(required = false) final AuthorizationProvider authProvider,
			@Autowired(required = false) final HeaderProvider headerProvider,
			final List<RestConsumer> exceptionConsumers, final BodyPublisherProvider pubProvider,
			final BodyHandlerProvider bodyHandlerProvider) {

		final ClientConfig base = clientConfig(connectTimeout, requestTimeout);

		return new ClientConfig() {

			@Override
			public Duration connectTimeout() {
				return base.connectTimeout();
			}

			@Override
			public Duration responseTimeout() {
				return base.responseTimeout();
			}

			@Override
			public AuthorizationProvider authProvider() {
				return authProvider;
			}

			@Override
			public HeaderProvider headerProvider() {
				return headerProvider;
			}

			@Override
			public List<RestConsumer> restConsumers() {
				return exceptionConsumers == null ? List.of() : exceptionConsumers;
			}

			@Override
			public BodyPublisherProvider bodyPublisherProvider() {
				return pubProvider;
			}

			@Override
			public BodyHandlerProvider bodyHandlerProvider() {
				return bodyHandlerProvider;
			}

		};
	}

	@Bean
	public JsonByJackson jacksonFn(final ObjectMapper objectMapper) {
		return new JsonByJackson(objectMapper);
	}

	@Bean
	public BodyPublisherProvider pubProvider(final JsonByJackson jacksonFn) {
		return req -> {
			if (req.body() == null) {
				return BodyPublishers.noBody();
			}
			if (req.contentType().toLowerCase().startsWith(HttpUtils.TEXT_PLAIN)) {
				return BodyPublishers.ofString(req.body().toString());
			}

			// Default to JSON.
			return BodyPublishers.ofString(jacksonFn.toJson(req.body()));
		};
	}

	@Bean
	public BodyHandlerProvider bodyHandlerProvider(final JsonByJackson jacksonFn) {
		return req -> {
			final var receiver = req.bodyReceiver();
			final Class<?> type = receiver == null ? void.class : receiver.type();

			if (type.isAssignableFrom(void.class) || type.isAssignableFrom(Void.class)) {
				return BodyHandlers.discarding();
			}

			return responseInfo -> {
				// Default to UTF-8 text
				return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), text -> {
	
					if (responseInfo.statusCode() >= 300) {
						return text;
					}
	
					final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE).get().toLowerCase();

					if (contentType.toLowerCase().startsWith(HttpUtils.TEXT_PLAIN)) {
						return text;
					}
	
					return jacksonFn.fromJson(text, receiver);
				});
			};
		};
	}

}
