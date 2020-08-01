package me.ehp246.aufrest.api.configuration;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import me.ehp246.aufrest.api.rest.AuthenticationProvider;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.core.reflection.ObjectToText;
import me.ehp246.aufrest.core.reflection.TextToObject;
import me.ehp246.aufrest.core.util.InvocationUtil;
import me.ehp246.aufrest.provider.httpclient.JdkClientProvider;
import me.ehp246.aufrest.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
public class ByRestConfiguration {

	@Bean
	public JdkClientProvider jdkClientProvider(final ClientConfig clientConfig,
			@Autowired(required = false) final AuthenticationProvider authProvider) {
		return new JdkClientProvider(HttpClient::newBuilder, HttpRequest::newBuilder, clientConfig, authProvider);
	}

	@Bean
	public JsonByJackson jsonByJackson(@Autowired(required = false) final ObjectMapper objectMapper) {
		return new JsonByJackson(Optional.ofNullable(objectMapper).orElseGet(() -> {
			final var newMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule())
					.registerModule(new MrBeanModule());

			var module = InvocationUtil.invokeWithDefault(
					() -> Class.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule.JavaTimeModule")
							.getDeclaredConstructor((Class<?>[]) null).newInstance(),
					null);

			Optional.ofNullable(module).map(m -> newMapper.registerModule((com.fasterxml.jackson.databind.Module) m));

			module = InvocationUtil
					.invokeWithDefault(() -> Class.forName("com.fasterxml.jackson.module.mrbean.MrBeanModule")
							.getDeclaredConstructor((Class<?>[]) null).newInstance(), null);

			Optional.ofNullable(module).map(m -> newMapper.registerModule((com.fasterxml.jackson.databind.Module) m));
			return objectMapper;
		}));
	}

	@Bean
	public TextToObject textToObject(final JsonByJackson jackson) {
		return jackson.getFromText();
	}

	@Bean
	public ObjectToText objectToText(final JsonByJackson jackson) {
		return jackson.getToText();
	}

	@Bean
	public ClientConfig clientConfig(
			@Value("${" + AufRestConstants.CONNECT_TIMEOUT + ":" + AufRestConstants.CONNECT_TIMEOUT_DEFAULT
					+ "}") final long connectTimeout,
			@Value("${" + AufRestConstants.RESPONSE_TIMEOUT + ":" + AufRestConstants.RESPONSE_TIMEOUT_DEFAULT
					+ "}") final long requestTimeout) {
		return new ClientConfig() {
			@Override
			public Duration connectTimeout() {
				return Duration.ofMillis(connectTimeout);
			}

			@Override
			public Duration requestTimeout() {
				return Duration.ofMillis(requestTimeout);
			}

		};
	}

}
