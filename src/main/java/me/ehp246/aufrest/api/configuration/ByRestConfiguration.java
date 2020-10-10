package me.ehp246.aufrest.api.configuration;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.TextContentConsumer;
import me.ehp246.aufrest.api.rest.TextContentProducer;
import me.ehp246.aufrest.core.util.OneUtil;
import me.ehp246.aufrest.provider.httpclient.JdkClientProvider;
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
 * @version 2.0
 */
public class ByRestConfiguration {

	@Bean
	public JdkClientProvider jdkClientProvider(final ClientConfig clientConfig,
			@Autowired(required = false) final AuthorizationProvider authProvider,
			@Autowired(required = false) final HeaderProvider headerProvider) {
		return new JdkClientProvider(clientConfig, authProvider, headerProvider);
	}

	@Bean
	public ClientConfig clientConfig(@Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":}") final String connectTimeout,
			@Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":}") final String requestTimeout,
			@Autowired(required = false) final ObjectMapper objectMapper) {

		final var jackson = new JsonByJackson(Optional.ofNullable(objectMapper).orElseGet(() -> {
			final var newMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			var module = OneUtil
					.orElse(() -> Class.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule.JavaTimeModule")
							.getDeclaredConstructor((Class<?>[]) null).newInstance(), null);

			Optional.ofNullable(module).map(m -> newMapper.registerModule((com.fasterxml.jackson.databind.Module) m));

			module = OneUtil.orElse(() -> Class.forName("com.fasterxml.jackson.module.mrbean.MrBeanModule")
					.getDeclaredConstructor((Class<?>[]) null).newInstance(), null);

			Optional.ofNullable(module).map(m -> newMapper.registerModule((com.fasterxml.jackson.databind.Module) m));
			return objectMapper;
		}));

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

			@Override
			public TextContentProducer contentProducer(String mediaType) {
				mediaType = mediaType.toLowerCase();
				if (mediaType.startsWith(HttpUtils.APPLICATION_JSON)) {
					return jackson::toText;
				}

				if (mediaType.startsWith(HttpUtils.TEXT_PLAIN)) {
					return Object::toString;
				}

				throw new RuntimeException("Un-supported media type: " + mediaType);
			}

			@Override
			public TextContentConsumer contentConsumer(String mediaType) {
				mediaType = mediaType.toLowerCase();
				if (mediaType.startsWith(HttpUtils.APPLICATION_JSON)) {
					return jackson::fromText;
				}
				if (mediaType.startsWith(HttpUtils.TEXT_PLAIN)) {
					return (text, receiver) -> text;
				}
				throw new RuntimeException("Un-supported media type: " + mediaType);
			}

		};
	}

}
