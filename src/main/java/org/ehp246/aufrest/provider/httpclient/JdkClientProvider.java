package org.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.ehp246.aufrest.api.rest.Authentication;
import org.ehp246.aufrest.api.rest.AuthenticationProvider;
import org.ehp246.aufrest.api.rest.HttpFn;
import org.ehp246.aufrest.api.rest.HttpFnConfig;
import org.ehp246.aufrest.api.rest.HttpFnProvider;
import org.ehp246.aufrest.api.rest.HttpUtil;
import org.ehp246.aufrest.api.rest.Request;
import org.ehp246.aufrest.api.rest.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

/**
 * @author Lei Yang
 *
 */
public class JdkClientProvider implements HttpFnProvider {
	private final static Logger LOGGER = LoggerFactory.getLogger(JdkClientProvider.class);

	private final Supplier<HttpClient.Builder> clientBuilderSupplier;
	private final Supplier<HttpRequest.Builder> reqBuilderSupplier;

	/**
	 * @param clientBuilderSupplier
	 */
	public JdkClientProvider(final Supplier<HttpClient.Builder> clientBuilderSupplier,
			final Supplier<HttpRequest.Builder> reqBuilderSupplier) {
		super();
		this.clientBuilderSupplier = clientBuilderSupplier;
		this.reqBuilderSupplier = reqBuilderSupplier;
	}

	@Override
	public HttpFn get(final HttpFnConfig clientConfig) {
		final var clientBuilder = clientBuilderSupplier.get();
		if (clientConfig.connectTimeout() != null) {
			clientBuilder.connectTimeout(clientConfig.connectTimeout());
		}

		return new HttpFn() {
			private final HttpClient client = clientBuilder.build();
			private final Optional<AuthenticationProvider> authProvider = Optional
					.ofNullable(clientConfig.authProvider());

			@Override
			public Supplier<Response> apply(final Request req) {
				final var uri = URI.create(req.uri());
				final var body = req.body();
				final var authHeader = authProvider.map(provider -> provider.get(uri)).map(Authentication::header)
						.filter(Predicate.not(String::isEmpty)).orElse(null);

				final var builder = newRequestBuilder()
						.method(req.method().toUpperCase(),
								body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body.toString()))
						.uri(uri);

				if (clientConfig.responseTimeout() != null) {
					builder.timeout(clientConfig.responseTimeout());
				}

				if (authHeader != null) {
					builder.header(HttpUtil.AUTHORIZATION, authHeader);
				}

				return () -> {
					final var httpRequest = builder.build();
					LOGGER.debug("Sending {} {}", req.method(), req.uri());
					LOGGER.trace("Sending {}", body == null ? null : body.toString());

					HttpResponse<?> httpResponse;
					try {
						httpResponse = client.send(httpRequest, req.bodyHandler());
					} catch (IOException | InterruptedException e) {
						LOGGER.error("Failed to receive response", e);
						throw new RuntimeException(e);
					}

					LOGGER.debug("Received for {} {}", req.method(), req.uri());
					LOGGER.trace("Received {}", httpResponse.body());

					return new JdkResponseImplementation(req, httpResponse);
				};
			}
		};
	}

	private HttpRequest.Builder newRequestBuilder() {
		return reqBuilderSupplier.get().header(HttpUtil.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpUtil.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
	}
}
