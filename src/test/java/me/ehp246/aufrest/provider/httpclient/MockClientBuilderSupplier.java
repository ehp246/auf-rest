package me.ehp246.aufrest.provider.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Supplier;

import org.mockito.Mockito;

import me.ehp246.aufrest.mock.MockResponse;

/**
 * @author Lei Yang
 *
 */
class MockClientBuilderSupplier {
	private int builderCount = 0;
	private final Supplier<HttpResponse<?>> responseSupplier;
	private HttpRequest req = null;

	MockClientBuilderSupplier() {
		super();
		this.responseSupplier = null;
	}

	MockClientBuilderSupplier(final Supplier<HttpResponse<?>> responseSupplier) {
		super();
		this.responseSupplier = responseSupplier;
	}

	@SuppressWarnings("unchecked")
	HttpClient.Builder builder() {
		builderCount++;
		req = null;

		final HttpClient client = Mockito.mock(HttpClient.class);

		try {
			Mockito.when(client.send(Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
				req = invocation.getArgument(0);
				return (HttpResponse<Object>) Optional.ofNullable(responseSupplier).map(Supplier::get)
						.orElseGet(MockResponse::new);
			});
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException();
		}

		final var builder = Mockito.mock(HttpClient.Builder.class);

		Mockito.when(builder.build()).thenReturn(client);

		return builder;
	}

	int builderCount() {
		return builderCount;
	}

	HttpRequest request() {
		return req;
	}
}
