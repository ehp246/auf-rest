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
import com.sun.nio.sctp.IllegalReceiveException;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.TextContentConsumer;
import me.ehp246.aufrest.api.rest.TextContentProducer;
import me.ehp246.aufrest.core.util.Utils;
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
 * @version 1.1
 * @since 1.0
 */
public class ByRestConfiguration {

	@Bean
	public JdkClientProvider jdkClientProvider(final ClientConfig clientConfig,
			@Autowired(required = false) final AuthorizationProvider authProvider,
			@Autowired(required = false) final HeaderProvider headerProvider) {
		return new JdkClientProvider(clientConfig, authProvider, headerProvider);
	}

	@Bean
	public ClientConfig clientConfig(
			@Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":" + AufRestConstants.CONNECT_TIMEOUT_DEFAULT
					+ "}") final String connectTimeout,
			@Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":" + AufRestConstants.RESPONSE_TIMEOUT_DEFAULT
					+ "}") final String requestTimeout,
			@Autowired(required = false) final ObjectMapper objectMapper) {

		final var jackson = new JsonByJackson(Optional.ofNullable(objectMapper).orElseGet(() -> {
			final var newMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			var module = Utils
					.orElse(() -> Class.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule.JavaTimeModule")
							.getDeclaredConstructor((Class<?>[]) null).newInstance(), null);

			Optional.ofNullable(module).map(m -> newMapper.registerModule((com.fasterxml.jackson.databind.Module) m));

			module = Utils.orElse(() -> Class.forName("com.fasterxml.jackson.module.mrbean.MrBeanModule")
					.getDeclaredConstructor((Class<?>[]) null).newInstance(), null);

			Optional.ofNullable(module).map(m -> newMapper.registerModule((com.fasterxml.jackson.databind.Module) m));
			return objectMapper;
		}));

		final var conTimeout = Utils.orThrow(() -> Duration.parse(connectTimeout),
				e -> new IllegalReceiveException("Invalid Connection Timeout: " + connectTimeout));

		final var reqTimeout = Utils.orThrow(() -> Duration.parse(requestTimeout),
				e -> new IllegalReceiveException("Invalid Response Timeout: " + requestTimeout));

		return new ClientConfig() {
			@Override
			public Duration connectTimeout() {
				return conTimeout;
			}

			@Override
			public Duration responseTimeout() {
				return reqTimeout;
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
