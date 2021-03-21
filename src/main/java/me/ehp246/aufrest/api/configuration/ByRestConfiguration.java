package me.ehp246.aufrest.api.configuration;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.AuthorizationProvider;
import me.ehp246.aufrest.api.rest.BodyFn;
import me.ehp246.aufrest.api.rest.BodyReceiver;
import me.ehp246.aufrest.api.rest.BodySupplier;
import me.ehp246.aufrest.api.rest.ClientConfig;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RequestFilter;
import me.ehp246.aufrest.api.rest.RequestLogger;
import me.ehp246.aufrest.api.rest.ResponseFilter;
import me.ehp246.aufrest.api.rest.TextBodyFn;
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
 * @version 2.1.1
 */
@Import(JdkClientProvider.class)
public class ByRestConfiguration {

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
			@Autowired(required = false) final HeaderProvider headerProvider, final Set<BodyFn> bodyFns,
			final List<RequestFilter> requestFilters, final List<ResponseFilter> responseFilters) {

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
			public Set<BodyFn> bodyFns() {
				return bodyFns;
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
			public List<RequestFilter> requestFilters() {
				return requestFilters == null ? List.of() : requestFilters;
			}

			@Override
			public List<ResponseFilter> responseFilters() {
				return responseFilters == null ? List.of() : responseFilters;
			}

		};
	}

	@Bean
	public TextBodyFn jacksonFn(final ObjectMapper objectMapper) {
		return new JsonByJackson(objectMapper);
	}

	@Bean
	public TextBodyFn plainTextFn() {
		return new TextBodyFn() {

			@Override
			public boolean accept(final String contentType) {
				return contentType.toLowerCase().startsWith(HttpUtils.TEXT_PLAIN);
			}

			@Override
			public String toText(final BodySupplier supplier) {
				return supplier.get().toString();
			}

			@Override
			public Object fromText(final String body, final BodyReceiver receiver) {
				return body.toString();
			}
		};
	}

	@Bean
	public RequestFilter loggingRequestFilter() {
		return new RequestLogger();
	}
}
