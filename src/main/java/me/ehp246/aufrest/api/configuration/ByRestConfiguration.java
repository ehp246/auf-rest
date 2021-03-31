package me.ehp246.aufrest.api.configuration;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.BodyPublisherProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.api.spi.PlaceholderResolver;
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
	@Bean
	public ClientConfig clientConfig(@Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout,
			@Autowired(required = false)
			final BodyHandlerProvider bodyHandlerProvider) {
		final var connTimeout = Optional.ofNullable(connectTimeout).filter(OneUtil::hasValue)
				.map(value -> OneUtil.orThrow(() -> Duration.parse(value),
						e -> new IllegalArgumentException("Invalid Connection Timeout: " + value)))
				.orElse(null);

		return new ClientConfig() {

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
						: req -> respInfo -> BodySubscribers.mapping(BodySubscribers.discarding(),
								body -> null);
			}

		};
	}

	@Bean
	public JsonByJackson jacksonFn(final ObjectMapper objectMapper) {
		return new JsonByJackson(objectMapper);
	}

	@Bean
	public BodyPublisherProvider bodyPublisherProvider(final JsonByJackson jacksonFn) {
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
				// The server might not set the header. Treat it as text?
				final var contentType = responseInfo.headers().firstValue(HttpUtils.CONTENT_TYPE).orElse("")
						.toLowerCase();
				// Default to UTF-8 text
				return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), text -> {
					if (responseInfo.statusCode() >= 300) {
						return text;
					}
					if (contentType.startsWith(HttpUtils.APPLICATION_JSON)) {
						return jacksonFn.fromJson(text, receiver);
					}
					return text;
				});
			};
		};
	}

	@Bean
	RestLogger restLogger(final ObjectMapper objectMapper) {
		return new RestLogger(objectMapper);
	}

	@Bean
	public PlaceholderResolver placeholderResolver(final Environment env) {
		return env::resolveRequiredPlaceholders;
	}

	@Bean
	RequestBuilder requestBuilder(@Autowired(required = false) final HeaderProvider headerProvider,
			@Autowired(required = false) final AuthProvider authProvider,
			@Autowired(required = false) final BodyPublisherProvider bodyPublisherProvider,
			@Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout) {
		return new DefaultRequestBuilder(HttpRequest::newBuilder, headerProvider, authProvider, bodyPublisherProvider,
				requestTimeout);
	}
}
